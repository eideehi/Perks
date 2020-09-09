package net.eidee.minecraft.perks.perk;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.network.Networks;
import net.eidee.minecraft.perks.network.message.DisplayToast;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.network.PacketDistributor;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkController
{
    private final PlayerEntity player;
    private final PerkEnergyHandler energy;
    private final PerkExperienceHandler experience;
    private final PerkDataHandler data;

    private final Perk targetPerk;
    private PerkData referenceData;

    public PerkController( PlayerEntity player,
                           PerkEnergyHandler energy,
                           PerkExperienceHandler experience,
                           PerkDataHandler data,
                           Perk targetPerk )
    {
        this.player = player;
        this.energy = energy;
        this.experience = experience;
        this.data = data;
        this.targetPerk = targetPerk;

        this.updateReference();
    }

    private boolean usePerk( int rank, boolean simulation )
    {
        if ( !targetPerk.isAvailable() || !referenceData.isReady() || rank > referenceData.getRank() )
        {
            return false;
        }

        double cost = getUsageCost( rank );
        if ( cost > 0 )
        {
            double energy = this.energy.getEnergy();
            if ( energy >= cost )
            {
                this.energy.extractEnergy( cost, simulation || player.abilities.isCreativeMode );
                return true;
            }
        }
        // If the cost is negative, treat it as a failure.
        return cost == 0;
    }

    public void updateReference()
    {
        referenceData = data.getPerkData( targetPerk );
    }

    public double getEnergy()
    {
        return energy.getEnergy();
    }

    public int getExperience()
    {
        return experience.getExperience();
    }

    public Perk getTargetPerk()
    {
        return targetPerk;
    }

    public PerkData getReferenceData()
    {
        return referenceData;
    }

    public boolean removePerk()
    {
        if ( !targetPerk.isAvailable() )
        {
            return false;
        }

        boolean removed = data.removePerkData( ( key, value ) -> key.equals( targetPerk ) );
        updateReference();

        return removed;
    }

    public boolean unlockPerk( boolean force )
    {
        if ( !targetPerk.isAvailable() || referenceData.isLearned() || ( !force && !referenceData.isUnlocked() ) )
        {
            return false;
        }

        if ( !referenceData.isUnlocked() )
        {
            data.putPerkData( targetPerk, referenceData.withUnlockProgress( 1 ) );
            updateReference();
        }

        data.putPerkData( targetPerk, referenceData.withEnabled( true ) );
        updateReference();

        if ( !player.getEntityWorld().isRemote() && player instanceof ServerPlayerEntity )
        {
            PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with( () -> ( ServerPlayerEntity )player );
            Networks.getChannel().send( target, new DisplayToast( targetPerk ) );
        }

        return true;
    }

    public boolean addUnlockProgress( double amount )
    {
        if ( !targetPerk.isAvailable() || referenceData.isUnlocked() )
        {
            return false;
        }

        double newProgress = MathHelper.clamp( referenceData.getUnlockProgress() + amount, 0, 1 );
        data.putPerkData( targetPerk, referenceData.withUnlockProgress( newProgress ) );
        updateReference();

        return unlockPerk( false );
    }

    public boolean addUnlockProgress()
    {
        return addUnlockProgress( 1.0 / targetPerk.getLearningDifficulty() );
    }

    public boolean canUsePerk()
    {
        return usePerk( referenceData.getRank(), true );
    }

    public boolean usePerk()
    {
        return usePerk( referenceData.getRank(), false );
    }

    public boolean usePerk( int rank )
    {
        return usePerk( rank, false );
    }

    public double getUsageCost( int rank )
    {
        if ( !targetPerk.isAvailable() || rank < 0 )
        {
            return -1;
        }

        double cost = targetPerk.getUsageCost( rank );
        if ( cost < 0 )
        {
            return -1;
        }

        if ( cost > 0 && Perks.ECONOMY.isAvailable() )
        {
            PerkController economy = new PerkController( player, energy, experience, data, Perks.ECONOMY );
            if ( economy.getReferenceData().isReady() )
            {
                switch ( economy.getReferenceData().getRank() )
                {
                    default:
                    case 1:
                        return Math.max( cost / 1.10, 0.1 );

                    case 2:
                        return Math.max( cost / 1.15, 0.1 );

                    case 3:
                        return Math.max( cost / 1.20, 0.1 );
                }
            }
        }

        return cost;
    }

    public int getLearningCost( int rank )
    {
        if ( !targetPerk.isAvailable() || !referenceData.isUnlocked() )
        {
            return -1;
        }

        int cost = targetPerk.getLearningCost( rank );
        if ( cost < 0 )
        {
            return -1;
        }

        if ( cost > 0 && Perks.INTELLIGENCE.isAvailable() )
        {
            PerkController intelligence = new PerkController( player, energy, experience, data, Perks.INTELLIGENCE );
            if ( intelligence.getReferenceData().isReady() )
            {
                switch ( intelligence.getReferenceData().getRank() )
                {
                    default:
                    case 1:
                        return Math.max( ( int )( cost / 1.05 ), 1 );

                    case 2:
                        return Math.max( ( int )( cost / 1.10 ), 1 );

                    case 3:
                        return Math.max( ( int )( cost / 1.15 ), 1 );
                }
            }
        }

        return cost;
    }

    public boolean learningNextRank( boolean simulation )
    {
        if ( !targetPerk.isAvailable() || !referenceData.isUnlocked() )
        {
            return false;
        }

        int max = referenceData.getLearnedMaxRank();
        int rank = max + 1;
        int cost = getLearningCost( rank );
        if ( cost < 0 || experience.getExperience() < cost )
        {
            return false;
        }

        experience.extractExperience( cost, simulation );

        if ( !simulation )
        {
            data.putPerkData( targetPerk, referenceData.withLearnedMaxRank( rank ) );
            updateReference();

            if ( max == referenceData.getRank() )
            {
                data.putPerkData( targetPerk, referenceData.withRank( rank ) );
                updateReference();
            }
        }

        return true;
    }

    public boolean updateRank( int newRank, boolean simulation )
    {
        if ( !targetPerk.isAvailable() ||
             !referenceData.isLearned() ||
             newRank <= 0 ||
             newRank == referenceData.getRank() ||
             newRank > referenceData.getLearnedMaxRank() )
        {
            return false;
        }

        if ( !simulation )
        {
            data.putPerkData( targetPerk, referenceData.withRank( newRank ) );
            updateReference();
        }

        return true;
    }

    public boolean updateEnabled( boolean enabled, boolean simulation )
    {
        if ( !targetPerk.isAvailable() || !referenceData.isLearned() || enabled == referenceData.isEnabled() )
        {
            return false;
        }

        if ( !simulation )
        {
            data.putPerkData( targetPerk, referenceData.withEnabled( enabled ) );
            updateReference();
        }

        return true;
    }
}
