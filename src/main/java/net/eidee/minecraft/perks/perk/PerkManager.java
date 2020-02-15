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

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.capability.Capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkManager
{
    private static final LoadingCache< PlayerEntity, PerkManager > managerCache;

    static
    {
        managerCache = CacheBuilder.newBuilder().weakKeys().build( CacheLoader.from( PerkManager::newInstance ) );
    }

    private PlayerEntity player;
    private PerkEnergy perkEnergy;
    private PerkExperience perkExperience;
    private PerkStorage perkStorage;
    private boolean perkEnergyDirty;

    private PerkManager( PlayerEntity player,
                         PerkEnergy perkEnergy,
                         PerkExperience perkExperience,
                         PerkStorage perkStorage )
    {
        this.player = player;
        this.perkEnergy = perkEnergy;
        this.perkExperience = perkExperience;
        this.perkStorage = perkStorage;
    }

    private static double getIncreaseBasePoint( Perk.Rarity rarity )
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

    private static PerkManager newInstance( PlayerEntity player )
    {
        PerkEnergy perkEnergy = player.getCapability( Capabilities.PERK_ENERGY )
                                      .orElseThrow( IllegalStateException::new );
        PerkExperience perkExperience = player.getCapability( Capabilities.PERK_EXPERIENCE )
                                              .orElseThrow( IllegalStateException::new );
        PerkStorage perkStorage = player.getCapability( Capabilities.PERK_STORAGE )
                                        .orElseThrow( IllegalStateException::new );
        return new PerkManager( player, perkEnergy, perkExperience, perkStorage );
    }

    public static PerkManager of( PlayerEntity player )
    {
        try
        {
            return managerCache.get( player );
        }
        catch ( ExecutionException e )
        {
            throw new RuntimeException( e );
        }
    }

    private boolean usePerk( Perk perk, PerkData data, int rank, boolean simulation )
    {
        if ( data.isLearned() && data.isEnabled() && rank <= data.getRank() )
        {
            int usageCost = getUsageCost( perk, rank );
            if ( usageCost > 0 )
            {
                double energyValue = perkEnergy.getValue();
                if ( energyValue >= usageCost )
                {
                    if ( !simulation && !player.abilities.isCreativeMode )
                    {
                        perkEnergy.setValue( energyValue - usageCost );
                        perkEnergyDirty = true;
                    }
                    return true;
                }
            }
            else
            {
                return usageCost == 0;
            }
        }
        return false;
    }

    public PlayerEntity getPlayer()
    {
        return player;
    }

    public PerkEnergy getPerkEnergy()
    {
        return perkEnergy;
    }

    public PerkExperience getPerkExperience()
    {
        return perkExperience;
    }

    public PerkStorage getPerkStorage()
    {
        return perkStorage;
    }

    public boolean isPerkEnergyDirty()
    {
        return perkEnergyDirty;
    }

    public void clearPerkEnergyDirty()
    {
        this.perkEnergyDirty = false;
    }

    public void addPerkExperience( int value )
    {
        perkExperience.addValue( value );
    }

    public void addPerkEnergyBase( boolean permanent, double value )
    {
        PerkEnergy.BasePoint basePoint = new PerkEnergy.BasePoint( permanent, value );
        perkEnergy.addBasePoint( basePoint );
    }

    public void recoverPerkEnergy( int value )
    {
        double currentValue = perkEnergy.getValue();
        double newValue = Math.min( currentValue + value, perkEnergy.getMax() );
        if ( currentValue != newValue )
        {
            perkEnergy.setValue( newValue );
            perkEnergyDirty = true;
        }
    }

    public boolean progressUnlock( Perk perk, double progressValue )
    {
        PerkData data = perkStorage.getData( perk );
        if ( !data.isUnlocked() )
        {
            double newProgressValue = data.getUnlockProgress() + progressValue;
            data.setUnlockProgress( MathHelper.clamp( newProgressValue, 0.0, 1.0 ) );
            if ( data.isUnlocked() )
            {
                double increase = getIncreaseBasePoint( perk.getRarity() );
                PerkEnergy.BasePoint basePoint = new PerkEnergy.BasePoint( true, increase );
                perkEnergy.addBasePoint( basePoint );
                return true;
            }
        }
        return false;
    }

    public void removePerk( Perk perk )
    {
        PerkData remove = perkStorage.getRawStorage().remove( perk );
        if ( remove != null && remove.isUnlocked() )
        {
            double increase = getIncreaseBasePoint( perk.getRarity() );
            PerkEnergy.BasePoint basePoint = new PerkEnergy.BasePoint( true, increase );
            perkEnergy.removeBasePoint( basePoint );
        }
    }

    public boolean usePerk( Perk perk, boolean simulation )
    {
        PerkData data = perkStorage.getData( perk );
        return usePerk( perk, data, data.getRank(), simulation );
    }

    public boolean usePerk( Perk perk, int rank, boolean simulation )
    {
        PerkData data = perkStorage.getData( perk );
        return usePerk( perk, data, rank, simulation );
    }

    public int getLearningCost( Perk perk )
    {
        PerkData data = perkStorage.getData( perk );
        int learningRank = data.isUnlocked() ? data.getLearnedMaxRank() + 1 : -1;
        int learningCost = learningRank != -1 ? perk.getLearningCost( learningRank ) : -1;
        if ( learningCost > 0 )
        {
            PerkData intelligence = perkStorage.getData( Perks.INTELLIGENCE );
            if ( intelligence.isLearned() && intelligence.isEnabled() )
            {
                switch ( intelligence.getRank() )
                {
                    default:
                    case 1:
                        return Math.max( 1, ( int )( learningCost / 1.05 ) );

                    case 2:
                        return Math.max( 1, ( int )( learningCost / 1.10 ) );

                    case 3:
                        return Math.max( 1, ( int )( learningCost / 1.15 ) );
                }
            }
        }
        return learningCost;
    }

    public int getUsageCost( Perk perk, int rank )
    {
        if ( rank < 0 )
        {
            return -1;
        }
        int usageCost = perk.getUsageCost( rank );
        if ( usageCost > 0 )
        {
            PerkData economy = perkStorage.getData( Perks.ECONOMY );
            if ( economy.isLearned() && economy.isEnabled() )
            {
                switch ( economy.getRank() )
                {
                    default:
                    case 1:
                        return Math.max( 1, ( int )( usageCost / 1.10 ) );

                    case 2:
                        return Math.max( 1, ( int )( usageCost / 1.15 ) );

                    case 3:
                        return Math.max( 1, ( int )( usageCost / 1.20 ) );
                }
            }
        }
        return usageCost;
    }

    public boolean learningNextRank( Perk perk )
    {
        PerkData data = perkStorage.getData( perk );
        if ( data.isUnlocked() )
        {
            int learnedMaxRank = data.getLearnedMaxRank();
            int learningRank = learnedMaxRank + 1;
            if ( learningRank <= perk.getMaxRank() )
            {
                int learningCost = getLearningCost( perk );
                if ( perkExperience.getValue() >= learningCost )
                {
                    perkExperience.removeValue( learningCost );

                    data.setLearnedMaxRank( learningRank );
                    if ( learnedMaxRank == data.getRank() )
                    {
                        data.setRank( learningRank );
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean changeRank( Perk perk, int newRank )
    {
        PerkData data = perkStorage.getData( perk );
        if ( data.isLearned() )
        {
            if ( newRank >= 1 && newRank <= data.getLearnedMaxRank() && newRank != data.getRank() )
            {
                data.setRank( newRank );
                return true;
            }
        }
        return false;
    }

    public boolean setPerkEnable( Perk perk, boolean enable )
    {
        PerkData data = perkStorage.getData( perk );
        if ( data.isLearned() )
        {
            data.setEnabled( enable );
            return true;
        }
        return false;
    }

    public void resetPerkEnergy()
    {
        perkEnergy.setValue( 0.0 );
        List< PerkEnergy.BasePoint > basePoints = perkEnergy.getBasePoints();
        perkEnergy.setBasePoints( basePoints.stream()
                                            .filter( PerkEnergy.BasePoint::isPermanent )
                                            .collect( Collectors.toList() ) );
    }

    public void tick()
    {
        double _max = perkEnergy.getMax();
        double basePoint = perkEnergy.getBasePoint();
        double max = basePoint + ( basePoint * ( player.experienceLevel * 0.025 ) );
        if ( max != _max )
        {
            perkEnergy.setMax( max );
            perkEnergyDirty = ( perkEnergyDirty || ( int )max != ( int )_max );
        }

        double _value = perkEnergy.getValue();
        double value = Math.min( _value, max );
        if ( value != _value )
        {
            perkEnergy.setValue( value );
            perkEnergyDirty = ( perkEnergyDirty || ( int )value != ( int )_value );
        }

        if ( value < max )
        {
            _value = value;
            double recoveryRate = 600.0;
            recoveryRate += 1200 * ( player.experienceLevel * 0.005 );
            value += ( max / recoveryRate );
            perkEnergy.setValue( value );
            perkEnergyDirty = ( perkEnergyDirty || ( int )value != ( int )_value );
        }
    }
}
