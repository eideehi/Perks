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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class Sync< NBT extends INBT >
{
    private static final String NBT_WRAP_KEY = "NBTWrapKey";

    private final CompoundNBT wrappedNBT;

    protected Sync( @Nullable CompoundNBT data )
    {
        this.wrappedNBT = data != null ? data : new CompoundNBT();
    }

    static CompoundNBT wrapNBT( @Nullable INBT nbt )
    {
        INBT _nbt = nbt != null ? nbt : new CompoundNBT();
        if ( _nbt instanceof CompoundNBT )
        {
            CompoundNBT compound = ( CompoundNBT )_nbt;
            if ( compound.contains( NBT_WRAP_KEY ) )
            {
                return compound;
            }
        }
        CompoundNBT compound = new CompoundNBT();
        compound.put( NBT_WRAP_KEY, _nbt );
        return compound;
    }

    protected abstract NBT unwrapCast( @Nullable INBT nbt );

    protected NBT unwrapNBT( CompoundNBT nbt )
    {
        if ( !nbt.contains( NBT_WRAP_KEY ) )
        {
            throw new IllegalArgumentException();
        }
        return unwrapCast( nbt.get( NBT_WRAP_KEY ) );
    }

    public final CompoundNBT getWrappedNBT()
    {
        return wrappedNBT;
    }

    public final NBT getRawData()
    {
        return unwrapNBT( wrappedNBT );
    }

    public static class PerkEnergy
        extends Sync< CompoundNBT >
    {
        public PerkEnergy( @Nullable CompoundNBT data )
        {
            super( wrapNBT( data ) );
        }

        public static void encode( PerkEnergy message, PacketBuffer buffer )
        {
            buffer.writeCompoundTag( message.getWrappedNBT() );
        }

        public static PerkEnergy decode( PacketBuffer buffer )
        {
            return new PerkEnergy( buffer.readCompoundTag() );
        }

        @Override
        protected CompoundNBT unwrapCast( @Nullable INBT nbt )
        {
            if ( nbt instanceof CompoundNBT )
            {
                return ( CompoundNBT )nbt;
            }
            throw new IllegalStateException();
        }
    }

    public static class PerkExperience
        extends Sync< IntNBT >
    {
        public PerkExperience( @Nullable CompoundNBT data )
        {
            super( data );
        }

        public PerkExperience( IntNBT data )
        {
            this( wrapNBT( data ) );
        }

        public static void encode( PerkExperience message, PacketBuffer buffer )
        {
            buffer.writeCompoundTag( message.getWrappedNBT() );
        }

        public static PerkExperience decode( PacketBuffer buffer )
        {
            return new PerkExperience( buffer.readCompoundTag() );
        }

        @Override
        protected IntNBT unwrapCast( @Nullable INBT nbt )
        {
            if ( nbt instanceof IntNBT )
            {
                return ( IntNBT )nbt;
            }
            throw new IllegalStateException();
        }
    }

    public static class PerkStorage
        extends Sync< ListNBT >
    {
        public PerkStorage( @Nullable CompoundNBT data )
        {
            super( data );
        }

        public PerkStorage( ListNBT data )
        {
            this( wrapNBT( data ) );
        }

        public static void encode( PerkStorage message, PacketBuffer buffer )
        {
            buffer.writeCompoundTag( message.getWrappedNBT() );
        }

        public static PerkStorage decode( PacketBuffer buffer )
        {
            return new PerkStorage( buffer.readCompoundTag() );
        }

        @Override
        protected ListNBT unwrapCast( @Nullable INBT nbt )
        {
            if ( nbt instanceof ListNBT )
            {
                return ( ListNBT )nbt;
            }
            throw new IllegalStateException();
        }
    }
}
