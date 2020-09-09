package net.eidee.minecraft.perks.registry;

import java.util.Map;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Maps;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.perk.PerkEnergyBoost;
import net.eidee.minecraft.perks.perk.PerkExtraData;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkExtraDataRegistry
{
    private static final Map< String, PerkExtraData< ? > > extraDataMap;
    private static boolean lock;

    static
    {
        extraDataMap = Maps.newHashMap();
    }

    private PerkExtraDataRegistry()
    {
    }

    public static Optional< PerkExtraData< ? > > getExtraData( String name )
    {
        return Optional.ofNullable( extraDataMap.get( name ) );
    }

    public static boolean isLocked()
    {
        return lock;
    }

    public static void lock()
    {
        lock = true;
    }

    public static void registerPerkExtraData()
    {
        registerPerkExtraData( PerkEnergyBoost.ExtraData.INSTANCE );
    }

    public static < T > void registerPerkExtraData( PerkExtraData< T > extraData )
    {
        String name = extraData.getName();

        if ( lock )
        {
            PerksMod.getLogger().error( "Attempted to register data {} even though registry phase is over", name );
            throw new IllegalArgumentException( "Registration of perk extra data is locked" );
        }

        if ( extraDataMap.containsKey( name ) )
        {
            PerksMod.getLogger().error( "PerkExtraData {} already registered", name );
            throw new IllegalArgumentException( "PerkExtraData {" + name + "} already registered" );
        }

        extraDataMap.put( name, extraData );
        PerksMod.getLogger().info( "PerkExtraData {} has been registered.", name );
    }
}
