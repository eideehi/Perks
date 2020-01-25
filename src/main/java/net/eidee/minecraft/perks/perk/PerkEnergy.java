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
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Lists;
import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkEnergy
    implements INBTSerializable< CompoundNBT >
{
    private static final BasePoint DEFAULT_BASE_POINT = new BasePoint( true, 100 );

    private double max;
    private double value;
    private double basePoint;
    private List< BasePoint > basePoints;
    private boolean basePointDirty;

    public PerkEnergy()
    {
        max = 0.0;
        value = 0.0;
        basePoint = 0.0;
        basePoints = Lists.newArrayList( DEFAULT_BASE_POINT );
        basePointDirty = true;
    }

    void setMax( double max )
    {
        this.max = max;
    }

    void setValue( double value )
    {
        this.value = value;
    }

    List< BasePoint > getBasePoints()
    {
        return basePoints;
    }

    void setBasePoints( List< BasePoint > basePoints )
    {
        this.basePoints = Lists.newArrayList( basePoints );
        this.basePointDirty = true;
    }

    void addBasePoint( BasePoint basePoint )
    {
        basePoints.add( basePoint );
        basePointDirty = true;
    }

    void removeBasePoint( BasePoint basePoint )
    {
        basePoints.remove( basePoint );
        basePointDirty = true;
    }

    public double getMax()
    {
        return max;
    }

    public double getValue()
    {
        return value;
    }

    public double getBasePoint()
    {
        if ( basePointDirty )
        {
            basePoint = basePoints.stream().mapToDouble( BasePoint::getValue ).sum();
            basePointDirty = false;
        }
        return basePoint;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT compound = new CompoundNBT();
        compound.putDouble( "Max", max );
        compound.putDouble( "Value", value );
        compound.putDouble( "BasePoint", basePoint );
        ListNBT list = new ListNBT();
        basePoints.forEach( basePoint -> {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putBoolean( "Permanent", basePoint.isPermanent() );
            nbt.putDouble( "Value", basePoint.getValue() );
            list.add( nbt );
        } );
        compound.put( "BasePoints", list );
        return compound;
    }

    @Override
    public void deserializeNBT( CompoundNBT nbt )
    {
        this.max = nbt.getDouble( "Max" );
        this.value = nbt.getDouble( "Value" );
        this.basePoint = nbt.getDouble( "BasePoint" );
        this.basePoints = Lists.newArrayList();
        ListNBT list = nbt.getList( "BasePoints", Constants.NBT.TAG_COMPOUND );
        for ( int i = 0; i < list.size(); i++ )
        {
            CompoundNBT compound = list.getCompound( i );
            boolean permanent = compound.getBoolean( "Permanent" );
            double value = compound.getDouble( "Value" );
            basePoints.add( new BasePoint( permanent, value ) );
        }
        this.basePointDirty = true;
    }

    public static class BasePoint
    {
        private final boolean permanent;
        private final double value;

        public BasePoint( boolean permanent, double value )
        {
            this.permanent = permanent;
            this.value = value;
        }

        public boolean isPermanent()
        {
            return permanent;
        }

        public double getValue()
        {
            return value;
        }

        @Override
        public boolean equals( Object o )
        {
            if ( this == o )
            {
                return true;
            }
            if ( o == null || getClass() != o.getClass() )
            {
                return false;
            }
            BasePoint basePoint = ( BasePoint )o;
            return permanent == basePoint.permanent && Double.compare( basePoint.value, value ) == 0;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash( permanent, value );
        }
    }
}
