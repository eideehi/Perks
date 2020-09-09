package net.eidee.minecraft.perks.perk;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface PerkEnergyHandler
{
    double getMaxEnergy();

    double getEnergy();

    void increaseMaxEnergy( double value );

    void decreaseMaxEnergy( double value );

    double receiveEnergy( double value, boolean simulate );

    double extractEnergy( double value, boolean simulate );

    boolean isDirty();

    void clearDirty();
}
