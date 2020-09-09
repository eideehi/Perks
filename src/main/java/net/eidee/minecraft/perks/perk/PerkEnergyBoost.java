package net.eidee.minecraft.perks.perk;

import static net.eidee.minecraft.perks.PerksMod.MOD_ID;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Lists;
import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.LazyOptional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkEnergyBoost
{
    private final String id;
    private final double value;
    private final boolean temporary;
    private final int lifespan;
    private int age;

    public PerkEnergyBoost( String id, double value, boolean temporary, int lifespan, int age )
    {
        this.id = id;
        this.value = value;
        this.temporary = temporary;
        this.lifespan = lifespan;
        this.age = age;
    }

    public String getId()
    {
        return id;
    }

    public double getValue()
    {
        return value;
    }

    public boolean isTemporary()
    {
        return temporary;
    }

    public int getLifespan()
    {
        return lifespan;
    }

    public int getAge()
    {
        return age;
    }

    public void aging()
    {
        age++;
    }

    public boolean isExpire()
    {
        return lifespan >= 0 && age >= lifespan;
    }

    public enum ExtraData
        implements PerkExtraData< List< PerkEnergyBoost > >
    {
        INSTANCE;

        public static final String NAME = ( MOD_ID + ':' + "perk_energy_boost" );

        @Override
        public String getName()
        {
            return NAME;
        }

        @Override
        public Provider getProvider()
        {
            List< PerkEnergyBoost > instance = Lists.newArrayList();
            return new Provider()
            {
                @Override
                public < T > LazyOptional< T > getExtraData( PerkExtraData< T > extraData )
                {
                    return INSTANCE.orEmpty( extraData, LazyOptional.of( () -> instance ) );
                }

                @Override
                public INBT serializeNBT()
                {
                    ListNBT nbt = new ListNBT();

                    instance.forEach( boost -> {
                        CompoundNBT compound = new CompoundNBT();
                        compound.putString( "ID", boost.getId() );
                        compound.putDouble( "Value", boost.getValue() );
                        compound.putBoolean( "Temporary", boost.isTemporary() );
                        compound.putInt( "Lifespan", boost.getLifespan() );
                        compound.putInt( "Age", boost.getAge() );
                        nbt.add( compound );
                    } );

                    return nbt;
                }

                @Override
                public void deserializeNBT( INBT nbt )
                {
                    if ( !( nbt instanceof ListNBT ) )
                    {
                        return;
                    }

                    ListNBT list = ( ListNBT )nbt;

                    instance.clear();
                    for ( int i = 0; i < list.size(); i++ )
                    {
                        CompoundNBT compound = list.getCompound( i );

                        instance.add( new PerkEnergyBoost( compound.getString( "ID" ),
                                                           compound.getDouble( "Value" ),
                                                           compound.getBoolean( "Temporary" ),
                                                           compound.getInt( "Lifespan" ),
                                                           compound.getInt( "Age" ) ) );
                    }
                }
            };
        }
    }
}
