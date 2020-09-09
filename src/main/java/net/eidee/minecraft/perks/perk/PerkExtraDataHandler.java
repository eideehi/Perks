package net.eidee.minecraft.perks.perk;

import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface PerkExtraDataHandler
{
    < T > Stream< T > getExtraData( PerkExtraData< T > extraData );

    < T > boolean removeExtraData( PerkExtraData< T > extraData );
}
