package net.eidee.minecraft.perks.capability;

import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.perk.PerkExperienceHandler;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkExperienceHandlerCapability
    implements PerkExperienceHandler,
               INBTSerializable< IntNBT >
{
    private int experience;
    private boolean dirty;

    public PerkExperienceHandlerCapability()
    {
        this.experience = 0;
        this.dirty = false;
    }

    public static Capability.IStorage< PerkExperienceHandler > storage()
    {
        return new Capability.IStorage< PerkExperienceHandler >()
        {
            @Override
            public INBT writeNBT( Capability< PerkExperienceHandler > capability,
                                  PerkExperienceHandler instance,
                                  Direction side )
            {
                if ( !( instance instanceof PerkExperienceHandlerCapability ) )
                {
                    throw new RuntimeException( "Cannot serialize due to a unknown implementation of the instance" );
                }
                return ( ( PerkExperienceHandlerCapability )instance ).serializeNBT();
            }

            @Override
            public void readNBT( Capability< PerkExperienceHandler > capability,
                                 PerkExperienceHandler instance,
                                 Direction side,
                                 INBT nbt )
            {
                if ( !( instance instanceof PerkExperienceHandlerCapability ) )
                {
                    throw new RuntimeException( "Cannot deserialize due to a unknown implementation of the instance" );
                }
                ( ( PerkExperienceHandlerCapability )instance ).deserializeNBT( ( IntNBT )nbt );
            }
        };
    }

    public static ICapabilityProvider provider()
    {
        Capability< PerkExperienceHandler > capability = Capabilities.PERK_EXPERIENCE;
        PerkExperienceHandler instance = capability.getDefaultInstance();
        LazyOptional< PerkExperienceHandler > lazyOpt = LazyOptional.of( instance == null ? null : () -> instance );
        return new ICapabilitySerializable< IntNBT >()
        {
            @Override
            public < T > LazyOptional< T > getCapability( Capability< T > cap, @Nullable Direction side )
            {
                return capability.orEmpty( cap, lazyOpt );
            }

            @Override
            public IntNBT serializeNBT()
            {
                return ( IntNBT )Objects.requireNonNull( capability.writeNBT( instance, null ) );
            }

            @Override
            public void deserializeNBT( IntNBT nbt )
            {
                capability.readNBT( instance, null, nbt );
            }
        };
    }

    @Override
    public int getExperience()
    {
        return experience;
    }

    @Override
    public int receiveExperience( int value, boolean simulate )
    {
        int result = Math.min( Integer.MAX_VALUE - experience, value );
        if ( !simulate )
        {
            experience += result;
            dirty = true;
        }
        return result;
    }

    @Override
    public int extractExperience( int value, boolean simulate )
    {
        int result = Math.min( experience, value );
        if ( !simulate )
        {
            experience -= result;
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
    public IntNBT serializeNBT()
    {
        return IntNBT.valueOf( experience );
    }

    @Override
    public void deserializeNBT( IntNBT nbt )
    {
        experience = nbt.getInt();
    }
}
