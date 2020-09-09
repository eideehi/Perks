package net.eidee.minecraft.perks.perk;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface PerkExtraData< T >
{
    String getName();

    Provider getProvider();

    default < R > LazyOptional< R > orEmpty( PerkExtraData< R > toCheck, LazyOptional< T > inst )
    {
        return this == toCheck ? inst.cast() : LazyOptional.empty();
    }

    interface Provider
        extends INBTSerializable< INBT >
    {
        < T > LazyOptional< T > getExtraData( PerkExtraData< T > extraData );
    }
}
