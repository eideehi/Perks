package net.eidee.minecraft.perks.eventhandler;

import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.capability.PerkDataHandlerCapability;
import net.eidee.minecraft.perks.capability.PerkEnergyHandlerCapability;
import net.eidee.minecraft.perks.capability.PerkExperienceHandlerCapability;
import net.eidee.minecraft.perks.capability.PerkExtraDataHandlerCapability;
import net.eidee.minecraft.perks.constants.RegistryKeys;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class CapabilityEventHandler
{
    @SubscribeEvent
    public static void attachCapabilitiesEntity( AttachCapabilitiesEvent< Entity > event )
    {
        Entity object = event.getObject();
        if ( object instanceof PlayerEntity )
        {
            event.addCapability( RegistryKeys.PERK_ENERGY, PerkEnergyHandlerCapability.provider() );

            event.addCapability( RegistryKeys.PERK_EXPERIENCE, PerkExperienceHandlerCapability.provider() );

            event.addCapability( RegistryKeys.PERK_DATA, PerkDataHandlerCapability.provider() );

            event.addCapability( RegistryKeys.PERK_EXTRA_DATA, PerkExtraDataHandlerCapability.provider() );
        }
    }

    @SubscribeEvent
    public static void attachCapabilitiesChunk( AttachCapabilitiesEvent< Chunk > event )
    {
        //event.addCapability( RegistryKeys.DIRTY_POSITION_LIST,
        //                     BasicProvider.create( Capabilities.DIRTY_POSITION_LIST, ListNBT.class ) );
    }
}
