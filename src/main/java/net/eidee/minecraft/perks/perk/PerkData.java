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

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.nbt.CompoundNBT;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkData
{
    private double unlockProgress;
    private int learnedMaxRank;
    private int rank;
    private boolean enabled;

    private PerkData( double unlockProgress, int learnedMaxRank, int rank, boolean enabled )
    {
        this.unlockProgress = unlockProgress;
        this.learnedMaxRank = learnedMaxRank;
        this.rank = rank;
        this.enabled = enabled;
    }

    void setUnlockProgress( double unlockProgress )
    {
        this.unlockProgress = unlockProgress;
    }

    void setLearnedMaxRank( int learnedMaxRank )
    {
        this.learnedMaxRank = learnedMaxRank;
    }

    void setRank( int rank )
    {
        this.rank = rank;
    }

    void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
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
        return unlockProgress >= 1.0;
    }

    public boolean isLearned()
    {
        return learnedMaxRank > 0;
    }

    public static PerkData createDefault()
    {
        return new PerkData( 0.0, 0, 0, true );
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
}
