/*
 * MIT License
 *
 * Copyright (c) 2020 EideeHi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.eidee.minecraft.perks.perk;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.capability.Capabilities;
import net.eidee.minecraft.perks.network.Networks;
import net.eidee.minecraft.perks.network.message.SyncCapability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkManager
{
    private static final LoadingCache< PlayerEntity, PerkManager > LOADED_MANAGERS;
    private static final PerkEnergyBoost.ExtraData ENERGY_BOOST;

    static
    {
        LOADED_MANAGERS = CacheBuilder.newBuilder().weakKeys().build( CacheLoader.from( PerkManager::newInstance ) );
        ENERGY_BOOST = PerkEnergyBoost.ExtraData.INSTANCE;
    }

    private final PlayerEntity player;
    private final PerkEnergyHandler energy;
    private final PerkExperienceHandler experience;
    private final PerkDataHandler data;
    private final PerkExtraDataHandler extra;

    private int lastLevel;
    private boolean requireSyncCapability;

    private PerkManager( PlayerEntity player,
                         PerkEnergyHandler energy,
                         PerkExperienceHandler experience,
                         PerkDataHandler data,
                         PerkExtraDataHandler extra )
    {
        this.player = player;
        this.energy = energy;
        this.experience = experience;
        this.data = data;
        this.extra = extra;
    }

    private static PerkManager newInstance( PlayerEntity player )
    {
        PerkEnergyHandler energy = player.getCapability( Capabilities.PERK_ENERGY )
                                         .orElseThrow( NoSuchElementException::new );

        PerkExperienceHandler experience = player.getCapability( Capabilities.PERK_EXPERIENCE )
                                                 .orElseThrow( NoSuchElementException::new );

        PerkDataHandler dataHandler = player.getCapability( Capabilities.PERK_DATA )
                                            .orElseThrow( NoSuchElementException::new );

        PerkExtraDataHandler extra = player.getCapability( Capabilities.PERK_EXTRA_DATA )
                                           .orElseThrow( NoSuchElementException::new );

        return new PerkManager( player, energy, experience, dataHandler, extra );
    }

    public static PerkManager get( PlayerEntity player )
    {
        try
        {
            return LOADED_MANAGERS.get( player );
        }
        catch ( ExecutionException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static PerkController createController( PlayerEntity player, Perk targetPerk )
    {
        return get( player ).createController( targetPerk );
    }

    @OnlyIn( Dist.CLIENT )
    public static Stream< PerkController > createControllerAll( PlayerEntity player )
    {
        PerkManager manager = get( player );
        return manager.data.getPerks().map( manager::createController );
    }

    public PerkController createController( Perk targetPerk )
    {
        return new PerkController( player, energy, experience, data, targetPerk );
    }


    private static double getBonusMaxEnergy( Perk.Rarity rarity )
    {
        switch ( rarity )
        {
            case COMMON:
                return 10.0;

            case RARE:
                return 20.0;

            case LEGENDARY:
                return 30.0;
        }
        return 0;
    }

    private void recalculateMaxEnergy()
    {
        DoubleList values = new DoubleArrayList();
        values.add( 100 );

        data.getPerks().map( this::createController ).forEach( controller -> {
            if ( controller.getReferenceData().isLearned() )
            {
                values.add( getBonusMaxEnergy( controller.getTargetPerk().getRarity() ) );
            }
        } );

        extra.getExtraData( ENERGY_BOOST ).flatMap( Collection::stream ).forEach( boost -> {
            if ( !boost.isExpire() )
            {
                values.add( boost.getValue() );
            }
        } );

        double newMaxEnergy = 0;
        double multiplier = 1 + ( player.experienceLevel * 0.025 );

        DoubleListIterator it = values.iterator();
        while ( it.hasNext() )
        {
            newMaxEnergy += ( it.nextDouble() * multiplier );
        }

        energy.decreaseMaxEnergy( energy.getMaxEnergy() );
        energy.increaseMaxEnergy( newMaxEnergy );
    }

    public void recoverEnergy( int value )
    {
        energy.receiveEnergy( value, false );
    }

    public void boostEnergy( String id, double value, boolean temporary, int lifespan )
    {
        extra.getExtraData( ENERGY_BOOST ).forEach( boosts -> {
            boosts.add( new PerkEnergyBoost( id, value, temporary, lifespan, 0 ) );
        } );
        recalculateMaxEnergy();
    }

    public void deathPenalty()
    {
        energy.extractEnergy( energy.getEnergy(), false );
        extra.getExtraData( ENERGY_BOOST ).forEach( boosts -> {
            boosts.removeIf( PerkEnergyBoost::isTemporary );
        } );
        recalculateMaxEnergy();
    }

    public void tick()
    {
        AtomicBoolean isDirty = new AtomicBoolean( false );

        boolean energyDirty = energy.isDirty();
        boolean experienceDirty = experience.isDirty();
        boolean dataDirty = data.isDirty();

        isDirty.set( dataDirty );

        int level = player.experienceLevel;
        isDirty.set( isDirty.get() || level != lastLevel );
        lastLevel = level;

        extra.getExtraData( ENERGY_BOOST ).forEach( boosts -> {
            boolean removed = boosts.removeIf( boost -> {
                boost.aging();
                return boost.isExpire();
            } );
            isDirty.set( isDirty.get() || removed );
        } );

        if ( isDirty.get() )
        {
            recalculateMaxEnergy();
        }

        double maxEnergy = energy.getMaxEnergy();
        double currentEnergy = energy.getEnergy();
        double newEnergy = currentEnergy;

        if ( newEnergy < maxEnergy )
        {
            double recoveryRate = 600.0 * ( 1 + ( player.experienceLevel * 0.01 ) );
            newEnergy += ( maxEnergy / recoveryRate );
        }

        newEnergy = Math.min( newEnergy, maxEnergy );

        if ( currentEnergy != newEnergy )
        {
            if ( newEnergy > currentEnergy )
            {
                energy.receiveEnergy( newEnergy - currentEnergy, false );
            }
            else
            {
                energy.extractEnergy( currentEnergy - newEnergy, false );
            }
        }

        if ( player instanceof ServerPlayerEntity )
        {
            if ( energyDirty || experienceDirty || dataDirty || requireSyncCapability )
            {
                PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with( () -> ( ServerPlayerEntity )player );

                if ( requireSyncCapability || energyDirty )
                {
                    Networks.getChannel().send( target, new SyncCapability.PerkEnergy( energy ) );
                    energy.clearDirty();
                }

                if ( requireSyncCapability || experienceDirty )
                {
                    Networks.getChannel().send( target, new SyncCapability.PerkExperience( experience ) );
                    experience.clearDirty();
                }

                if ( requireSyncCapability || dataDirty )
                {
                    Networks.getChannel().send( target, new SyncCapability.PerkData( data ) );
                    data.clearDirty();
                }
            }

            requireSyncCapability = false;
        }
    }

    public void syncCapabilities()
    {
        requireSyncCapability = true;
    }
}
