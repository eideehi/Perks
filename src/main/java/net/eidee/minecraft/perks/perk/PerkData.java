package net.eidee.minecraft.perks.perk;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.nbt.CompoundNBT;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkData
{
    public static final PerkData EMPTY = new PerkData( 0, 0, 0, false );

    private final double unlockProgress;
    private final int learnedMaxRank;
    private final int rank;
    private final boolean enabled;

    public PerkData( double unlockProgress, int learnedMaxRank, int rank, boolean enabled )
    {
        this.unlockProgress = unlockProgress;
        this.learnedMaxRank = learnedMaxRank;
        this.rank = rank;
        this.enabled = enabled;
    }

    public static CompoundNBT toNBT( PerkData data )
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putDouble( "UnlockProgress", data.getUnlockProgress() );
        nbt.putInt( "LearnedMaxRank", data.getLearnedMaxRank() );
        nbt.putInt( "Rank", data.getRank() );
        nbt.putBoolean( "Enabled", data.isEnabled() );
        return nbt;
    }

    public static PerkData fromNBT( CompoundNBT nbt )
    {
        return new PerkData( nbt.getDouble( "UnlockProgress" ),
                             nbt.getInt( "LearnedMaxRank" ),
                             nbt.getInt( "Rank" ),
                             nbt.getBoolean( "Enabled" ) );
    }

    public double getUnlockProgress()
    {
        return unlockProgress;
    }

    public int getLearnedMaxRank()
    {
        return learnedMaxRank;
    }

    public int getRank()
    {
        return rank;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public boolean isUnlocked()
    {
        return unlockProgress >= 1;
    }

    public boolean isLearned()
    {
        return isUnlocked() && learnedMaxRank > 0;
    }

    public boolean isReady()
    {
        return isLearned() && enabled;
    }

    public PerkData withUnlockProgress( double unlockProgress )
    {
        return new PerkData( unlockProgress, this.learnedMaxRank, this.rank, this.enabled );
    }

    public PerkData withLearnedMaxRank( int learnedMaxRank )
    {
        return new PerkData( this.unlockProgress, learnedMaxRank, this.rank, this.enabled );
    }

    public PerkData withRank( int rank )
    {
        return new PerkData( this.unlockProgress, this.learnedMaxRank, rank, this.enabled );
    }

    public PerkData withEnabled( boolean enabled )
    {
        return new PerkData( this.unlockProgress, this.learnedMaxRank, this.rank, enabled );
    }

    @Override
    public String toString()
    {
        return "PerkData{" +
               "unlockProgress=" +
               unlockProgress +
               ", learnedMaxRank=" +
               learnedMaxRank +
               ", rank=" +
               rank +
               ", enabled=" +
               enabled +
               '}';
    }
}
