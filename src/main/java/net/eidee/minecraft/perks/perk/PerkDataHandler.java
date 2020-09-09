package net.eidee.minecraft.perks.perk;

import java.util.function.BiPredicate;
import java.util.stream.Stream;

public interface PerkDataHandler
{
    Stream< Perk > getPerks();

    PerkData getPerkData( Perk perk );

    boolean putPerkData( Perk perk, PerkData data );

    boolean removePerkData( BiPredicate< Perk, PerkData > predicate );

    boolean isDirty();

    void clearDirty();
}
