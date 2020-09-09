package net.eidee.minecraft.perks.eventhandler;

import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.gui.PerkEnergyOverlayGui;
import net.eidee.minecraft.perks.gui.PerksScreen;
import net.eidee.minecraft.perks.settings.KeyBindings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn( Dist.CLIENT )
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class ClientSystemEventHandler
{
    private static final PerkEnergyOverlayGui OVERLAY = new PerkEnergyOverlayGui();

    @SubscribeEvent
    public static void keyInput( InputEvent.KeyInputEvent event )
    {
        InputMappings.Input mouseKey = InputMappings.getInputByCode( event.getKey(), event.getScanCode() );
        if ( KeyBindings.OPEN_PERK_GUI.isActiveAndMatches( mouseKey ) )
        {
            Minecraft.getInstance().displayGuiScreen( new PerksScreen() );
        }
    }

    @SubscribeEvent
    public static void renderGameOverlay( RenderGameOverlayEvent.Post event )
    {
        if ( event.getType() == RenderGameOverlayEvent.ElementType.TEXT )
        {
            OVERLAY.render( event.getMatrixStack() );
        }
    }
}
