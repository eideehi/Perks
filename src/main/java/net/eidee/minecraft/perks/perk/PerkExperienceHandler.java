package net.eidee.minecraft.perks.perk;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface PerkExperienceHandler
{
    int getExperience();

    int receiveExperience( int value, boolean simulate );

    int extractExperience( int value, boolean simulate );

    boolean isDirty();

    void clearDirty();
}
