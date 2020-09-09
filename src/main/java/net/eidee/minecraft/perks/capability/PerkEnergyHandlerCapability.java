package net.eidee.minecraft.perks.capability;

import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.perk.PerkEnergyHandler;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkEnergyHandlerCapability
    implements PerkEnergyHandler,
               INBTSerializable< CompoundNBT >
{
    private double maxEnergy;
    private double energy;
    private boolean dirty;

    public PerkEnergyHandlerCapability()
    {
        this.maxEnergy = 0;
        this.energy = 0;
        this.dirty = false;
    }

    public static Capability.IStorage< PerkEnergyHandler > storage()
    {
        return new Capability.IStorage< PerkEnergyHandler >()
        {
            @Override
            public INBT writeNBT( Capability< PerkEnergyHandler > capability,
                                  PerkEnergyHandler instance,
                                  Direction side )
            {
                if ( !( instance instanceof PerkEnergyHandlerCapability ) )
                {
                    throw new RuntimeException( "Cannot serialize due to a unknown implementation of the instance" );
                }
                return ( ( PerkEnergyHandlerCapability )instance ).serializeNBT();
            }

            @Override
            public void readNBT( Capability< PerkEnergyHandler > capability,
                                 PerkEnergyHandler instance,
                                 Direction side,
                                 INBT nbt )
            {
                if ( !( instance instanceof PerkEnergyHandlerCapability ) )
                {
                    throw new RuntimeException( "Cannot deserialize due to a unknown implementation of the instance" );
                }
                ( ( PerkEnergyHandlerCapability )instance ).deserializeNBT( ( CompoundNBT )nbt );
            }
        };
    }

    public static ICapabilityProvider provider()
    {
        Capability< PerkEnergyHandler > capability = Capabilities.PERK_ENERGY;
        PerkEnergyHandler instance = capability.getDefaultInstance();
        LazyOptional< PerkEnergyHandler > lazyOpt = LazyOptional.of( instance == null ? null : () -> instance );
        return new ICapabilitySerializable< CompoundNBT >()
        {
            @Override
            public < T > LazyOptional< T > getCapability( Capability< T > cap, @Nullable Direction side )
            {
                return capability.orEmpty( cap, lazyOpt );
            }

            @Override
            public CompoundNBT serializeNBT()
            {
                return ( CompoundNBT )Objects.requireNonNull( capability.writeNBT( instance, null ) );
            }

            @Override
            public void deserializeNBT( CompoundNBT nbt )
            {
                capability.readNBT( instance, null, nbt );
            }
        };
    }

    @Override
    public double getMaxEnergy()
    {
        return maxEnergy;
    }

    @Override
    public double getEnergy()
    {
        return energy;
    }

    @Override
    public void increaseMaxEnergy( double value )
    {
        double before = maxEnergy;
        maxEnergy += value;
        dirty = maxEnergy != before;
    }

    @Override
    public void decreaseMaxEnergy( double value )
    {
        double before = maxEnergy;
        maxEnergy -= value;
        dirty = maxEnergy != before;
    }

    @Override
    public double receiveEnergy( double value, boolean simulate )
    {
        double result = Math.min( maxEnergy - energy, value );
        if ( !simulate )
        {
            energy += result;
            dirty = true;
        }
        return result;
    }

    @Override
    public double extractEnergy( double value, boolean simulate )
    {
        double result = Math.min( energy, value );
        if ( !simulate )
        {
            energy -= result;
            dirty = true;
        }
        return result;
    }

    @Override
    public boolean isDirty()
    {
        return dirty;
    }

    @Override
    public void clearDirty()
    {
        dirty = false;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putDouble( "Max", maxEnergy );
        nbt.putDouble( "Value", energy );
        return nbt;
    }

    @Override
    public void deserializeNBT( CompoundNBT nbt )
    {
        maxEnergy = nbt.getDouble( "Max" );
        energy = nbt.getDouble( "Value" );
    }
}
