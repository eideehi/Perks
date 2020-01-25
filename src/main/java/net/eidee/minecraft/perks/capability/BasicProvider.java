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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BasicProvider< NBT extends INBT, TYPE extends INBTSerializable< NBT >, CAPABILITY extends Capability< TYPE > >
    implements ICapabilitySerializable< NBT >
{
    private final CAPABILITY capability;
    private final Function< ? super INBT, NBT > nbtCastFunction;
    private final TYPE instance;
    private Direction side;

    private BasicProvider( CAPABILITY capability, Function< ? super INBT, NBT > nbtCastFunction )
    {
        this.capability = capability;
        this.nbtCastFunction = nbtCastFunction;
        this.instance = capability.getDefaultInstance();
    }

    public static < NBT extends INBT, TYPE extends INBTSerializable< NBT >, CAPABILITY extends Capability< TYPE > > BasicProvider< NBT, TYPE, CAPABILITY > create(
        CAPABILITY capability,
        Class< NBT > clazz )
    {
        return new BasicProvider<>( capability, clazz::cast );
    }

    @Nonnull
    @Override
    public < T > LazyOptional< T > getCapability( @Nonnull Capability< T > cap, @Nullable Direction side )
    {
        this.side = side;
        return capability.orEmpty( cap, LazyOptional.of( () -> instance ) );
    }

    @Override
    public NBT serializeNBT()
    {
        return nbtCastFunction.apply( capability.writeNBT( instance, side ) );
    }

    @Override
    public void deserializeNBT( NBT nbt )
    {
        capability.readNBT( instance, side, nbt );
    }
}
