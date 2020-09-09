package net.eidee.minecraft.perks.capability;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Maps;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.perk.Perk;
import net.eidee.minecraft.perks.perk.PerkData;
import net.eidee.minecraft.perks.perk.PerkDataHandler;
import net.eidee.minecraft.perks.registry.PerkRegistry;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkDataHandlerCapability
    implements PerkDataHandler,
               INBTSerializable< ListNBT >
{
    private final Map< Perk, PerkData > dataMap;
    private boolean dirty;

    public PerkDataHandlerCapability()
    {
        this.dataMap = Maps.newHashMap();
        this.dirty = false;
    }

    public static Capability.IStorage< PerkDataHandler > storage()
    {
        return new Capability.IStorage< PerkDataHandler >()
        {
            @Override
            public INBT writeNBT( Capability< PerkDataHandler > capability, PerkDataHandler instance, Direction side )
            {
                if ( !( instance instanceof PerkDataHandlerCapability ) )
                {
                    throw new RuntimeException( "Cannot serialize due to a unknown implementation of the instance" );
                }
                return ( ( PerkDataHandlerCapability )instance ).serializeNBT();
            }

            @Override
            public void readNBT( Capability< PerkDataHandler > capability,
                                 PerkDataHandler instance,
                                 Direction side,
                                 INBT nbt )
            {
                if ( !( instance instanceof PerkDataHandlerCapability ) )
                {
                    throw new RuntimeException( "Cannot deserialize due to a unknown implementation of the instance" );
                }
                ( ( PerkDataHandlerCapability )instance ).deserializeNBT( ( ListNBT )nbt );
            }
        };
    }

    public static ICapabilityProvider provider()
    {
        Capability< PerkDataHandler > capability = Capabilities.PERK_DATA;
        PerkDataHandler instance = capability.getDefaultInstance();
        LazyOptional< PerkDataHandler > lazyOpt = LazyOptional.of( instance == null ? null : () -> instance );
        return new ICapabilitySerializable< ListNBT >()
        {
            @Override
            public < T > LazyOptional< T > getCapability( Capability< T > cap, @Nullable Direction side )
            {
                return capability.orEmpty( cap, lazyOpt );
            }

            @Override
            public ListNBT serializeNBT()
            {
                return ( ListNBT )Objects.requireNonNull( capability.writeNBT( instance, null ) );
            }

            @Override
            public void deserializeNBT( ListNBT nbt )
            {
                capability.readNBT( instance, null, nbt );
            }
        };
    }

    @Override
    public Stream< Perk > getPerks()
    {
        return dataMap.keySet().stream();
    }

    @Override
    public PerkData getPerkData( Perk perk )
    {
        return dataMap.getOrDefault( perk, PerkData.EMPTY );
    }

    @Override
    public boolean putPerkData( Perk perk, PerkData data )
    {
        PerkData old = dataMap.put( perk, data );
        dirty = !data.equals( old );
        return dirty;
    }

    @Override
    public boolean removePerkData( BiPredicate< Perk, PerkData > predicate )
    {
        dirty = dataMap.entrySet().removeIf( x -> predicate.test( x.getKey(), x.getValue() ) );
        return dirty;
    }

    @Override
    public boolean isDirty()
    {
        return dirty;
    }

    @Override
    public void clearDirty()
    {
        this.dirty = false;
    }

    @Override
    public ListNBT serializeNBT()
    {
        ListNBT nbt = new ListNBT();
        dataMap.forEach( ( key, value ) -> {
            ResourceLocation name = key.getRegistryName();
            if ( name != null )
            {
                CompoundNBT compound = new CompoundNBT();
                compound.putString( "Perk", name.toString() );
                compound.put( "PerkData", PerkData.toNBT( value ) );
                nbt.add( compound );
            }
        } );
        return nbt;
    }

    @Override
    public void deserializeNBT( ListNBT nbt )
    {
        dataMap.clear();
        for ( int i = 0; i < nbt.size(); i++ )
        {
            CompoundNBT compound = nbt.getCompound( i );
            PerkRegistry.getValue( compound.getString( "Perk" ) ).ifPresent( perk -> {
                dataMap.put( perk, PerkData.fromNBT( compound.getCompound( "PerkData" ) ) );
            } );
        }
    }
}
