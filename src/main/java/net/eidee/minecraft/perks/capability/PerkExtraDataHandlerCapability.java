package net.eidee.minecraft.perks.capability;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Maps;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.perk.PerkExtraData;
import net.eidee.minecraft.perks.perk.PerkExtraDataHandler;
import net.eidee.minecraft.perks.registry.PerkExtraDataRegistry;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkExtraDataHandlerCapability
    implements PerkExtraDataHandler,
               INBTSerializable< ListNBT >
{
    private Map< String, PerkExtraData.Provider > providers;

    public PerkExtraDataHandlerCapability()
    {
        this.providers = Maps.newHashMap();
    }

    public static Capability.IStorage< PerkExtraDataHandler > storage()
    {
        return new Capability.IStorage< PerkExtraDataHandler >()
        {
            @Override
            public INBT writeNBT( Capability< PerkExtraDataHandler > capability,
                                  PerkExtraDataHandler instance,
                                  Direction side )
            {
                if ( !( instance instanceof PerkExtraDataHandlerCapability ) )
                {
                    throw new RuntimeException( "Cannot serialize due to a unknown implementation of the instance" );
                }
                return ( ( PerkExtraDataHandlerCapability )instance ).serializeNBT();
            }

            @Override
            public void readNBT( Capability< PerkExtraDataHandler > capability,
                                 PerkExtraDataHandler instance,
                                 Direction side,
                                 INBT nbt )
            {
                if ( !( instance instanceof PerkExtraDataHandlerCapability ) )
                {
                    throw new RuntimeException( "Cannot deserialize due to a unknown implementation of the instance" );
                }
                ( ( PerkExtraDataHandlerCapability )instance ).deserializeNBT( ( ListNBT )nbt );
            }
        };
    }

    public static ICapabilityProvider provider()
    {
        Capability< PerkExtraDataHandler > capability = Capabilities.PERK_EXTRA_DATA;
        PerkExtraDataHandler instance = capability.getDefaultInstance();
        LazyOptional< PerkExtraDataHandler > lazyOpt = LazyOptional.of( instance == null ? null : () -> instance );
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
    public < T > Stream< T > getExtraData( PerkExtraData< T > extraData )
    {
        String name = extraData.getName();
        if ( !providers.containsKey( name ) )
        {
            Optional< PerkExtraData< ? > > opt = PerkExtraDataRegistry.getExtraData( name );
            providers.put( name, opt.orElseThrow( IllegalArgumentException::new ).getProvider() );
        }
        return providers.get( name ).getExtraData( extraData ).map( Stream::of ).orElse( Stream.empty() );
    }

    @Override
    public < T > boolean removeExtraData( PerkExtraData< T > extraData )
    {
        return providers.remove( extraData.getName() ) != null;
    }

    @Override
    public ListNBT serializeNBT()
    {
        ListNBT nbt = new ListNBT();
        providers.forEach( ( key, value ) -> {
            CompoundNBT compound = new CompoundNBT();
            compound.putString( "Name", key );
            compound.put( "Data", value.serializeNBT() );
            nbt.add( compound );
        } );
        return nbt;
    }

    @Override
    public void deserializeNBT( ListNBT nbt )
    {
        providers.clear();
        for ( int i = 0; i < nbt.size(); i++ )
        {
            CompoundNBT compound = nbt.getCompound( i );

            String name = compound.getString( "Name" );
            PerkExtraDataRegistry.getExtraData( name ).ifPresent( extraData -> {
                PerkExtraData.Provider provider = extraData.getProvider();
                provider.deserializeNBT( compound.get( "Data" ) );
                providers.put( name, provider );
            } );
        }
    }
}
