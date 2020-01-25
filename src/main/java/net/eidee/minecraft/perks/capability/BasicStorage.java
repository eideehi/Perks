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

package net.eidee.minecraft.perks.capability;

import java.util.function.Function;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BasicStorage< NBT extends INBT, TYPE extends INBTSerializable< NBT > >
    implements Capability.IStorage< TYPE >
{
    private final Function< ? super INBT, NBT > nbtCastFunction;

    private BasicStorage( Function< ? super INBT, NBT > nbtCastFunction )
    {
        this.nbtCastFunction = nbtCastFunction;
    }

    public static < NBT extends INBT, TYPE extends INBTSerializable< NBT > > BasicStorage< NBT, TYPE > create( Class< NBT > clazz )
    {
        return new BasicStorage<>( clazz::cast );
    }

    @Nullable
    @Override
    public INBT writeNBT( Capability< TYPE > capability, TYPE instance, Direction side )
    {
        return instance.serializeNBT();
    }

    @Override
    public void readNBT( Capability< TYPE > capability, TYPE instance, Direction side, INBT nbt )
    {
        instance.deserializeNBT( nbtCastFunction.apply( nbt ) );
    }
}
