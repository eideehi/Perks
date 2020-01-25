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

package net.eidee.minecraft.perks.network.message;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.network.PacketBuffer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkControl
{
    private final String perkId;

    protected PerkControl( String perkId )
    {
        this.perkId = perkId;
    }

    public final String getPerkId()
    {
        return perkId;
    }

    public static class UnlockPerk
        extends PerkControl
    {
        public UnlockPerk( String perkId )
        {
            super( perkId );
        }

        public static void encode( UnlockPerk message, PacketBuffer buffer )
        {
            buffer.writeString( message.getPerkId() );
        }

        public static UnlockPerk decode( PacketBuffer buffer )
        {
            return new UnlockPerk( buffer.readString() );
        }
    }

    public static class LearningNextRank
        extends PerkControl
    {
        public LearningNextRank( String perkId )
        {
            super( perkId );
        }

        public static void encode( LearningNextRank message, PacketBuffer buffer )
        {
            buffer.writeString( message.getPerkId() );
        }

        public static LearningNextRank decode( PacketBuffer buffer )
        {
            return new LearningNextRank( buffer.readString() );
        }
    }

    public static class ChangeRank
        extends PerkControl
    {
        private final int newRank;

        public ChangeRank( String perkId, int newRank )
        {
            super( perkId );
            this.newRank = newRank;
        }

        public final int getNewRank()
        {
            return newRank;
        }

        public static void encode( ChangeRank message, PacketBuffer buffer )
        {
            buffer.writeString( message.getPerkId() );
            buffer.writeInt( message.getNewRank() );
        }

        public static ChangeRank decode( PacketBuffer buffer )
        {
            return new ChangeRank( buffer.readString(), buffer.readInt() );
        }
    }

    public static class SetPerkEnable
        extends PerkControl
    {
        private final boolean enable;

        public SetPerkEnable( String perkId, boolean enable )
        {
            super( perkId );
            this.enable = enable;
        }

        public final boolean isEnable()
        {
            return enable;
        }

        public static void encode( SetPerkEnable message, PacketBuffer buffer )
        {
            buffer.writeString( message.getPerkId() );
            buffer.writeBoolean( message.isEnable() );
        }

        public static SetPerkEnable decode( PacketBuffer buffer )
        {
            return new SetPerkEnable( buffer.readString(), buffer.readBoolean() );
        }
    }

    public static class RemovePerk
        extends PerkControl
    {
        public RemovePerk( String perkId )
        {
            super( perkId );
        }

        public static void encode( RemovePerk message, PacketBuffer buffer )
        {
            buffer.writeString( message.getPerkId() );
        }

        public static RemovePerk decode( PacketBuffer buffer )
        {
            return new RemovePerk( buffer.readString() );
        }
    }
}
