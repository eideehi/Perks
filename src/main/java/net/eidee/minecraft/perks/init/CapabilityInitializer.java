package net.eidee.minecraft.perks.init;

import net.eidee.minecraft.perks.capability.PerkDataHandlerCapability;
import net.eidee.minecraft.perks.capability.PerkEnergyHandlerCapability;
import net.eidee.minecraft.perks.capability.PerkExperienceHandlerCapability;
import net.eidee.minecraft.perks.capability.PerkExtraDataHandlerCapability;
import net.eidee.minecraft.perks.perk.PerkDataHandler;
import net.eidee.minecraft.perks.perk.PerkEnergyHandler;
import net.eidee.minecraft.perks.perk.PerkExperienceHandler;
import net.eidee.minecraft.perks.perk.PerkExtraDataHandler;

import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityInitializer
{
    public static void registerCapability()
    {
        CapabilityManager instance = CapabilityManager.INSTANCE;

        instance.register( PerkEnergyHandler.class,
                           PerkEnergyHandlerCapability.storage(),
                           PerkEnergyHandlerCapability::new );

        instance.register( PerkExperienceHandler.class,
                           PerkExperienceHandlerCapability.storage(),
                           PerkExperienceHandlerCapability::new );

        instance.register( PerkDataHandler.class, PerkDataHandlerCapability.storage(), PerkDataHandlerCapability::new );

        instance.register( PerkExtraDataHandler.class,
                           PerkExtraDataHandlerCapability.storage(),
                           PerkExtraDataHandlerCapability::new );
    }
}
