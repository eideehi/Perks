/*
 * MIT License
 *
 * Copyright (c) 2020 EideeHi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.eidee.minecraft.perks.hooks.eventhandler.system;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.capability.Capabilities;
import net.eidee.minecraft.perks.settings.PerksConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn( Dist.CLIENT )
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class PerkEnergyEventHandler
{
    private static final PerkEnergyOverlay overlay = new PerkEnergyOverlay();

    @SubscribeEvent
    public static void renderGameOverlayEvent( RenderGameOverlayEvent.Post event )
    {
        if ( event.getType() == RenderGameOverlayEvent.ElementType.TEXT )
        {
            overlay.render();
        }
    }

    private static class PerkEnergyOverlay
        extends AbstractGui
    {
        private void render()
        {
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity player = minecraft.player;
            if ( player == null )
            {
                return;
            }
            int x = PerksConfig.CLIENT.perkEnergyUiX.get();
            int y = PerksConfig.CLIENT.perkEnergyUiY.get();
            player.getCapability( Capabilities.PERK_ENERGY ).ifPresent( perkEnergy -> {
                FontRenderer fontRenderer = minecraft.fontRenderer;
                int fontHeight = fontRenderer.FONT_HEIGHT;
                int fullHeight = fontHeight + 2;

                String label = I18n.format( "gui.perks.energy" );
                String max = I18n.format( "gui.perks.energy.value", Integer.MAX_VALUE );
                String value = I18n.format( "gui.perks.energy.value", ( int )perkEnergy.getValue() );
                int innerWidth = fontRenderer.getStringWidth( label ) + 1 + fontRenderer.getStringWidth( max );
                int outerWidth = 2 + innerWidth + 2;
                int gaugeWidthMax = fontRenderer.getStringWidth( max ) + 2;
                int gaugeWidth = ( int )( ( perkEnergy.getValue() / perkEnergy.getMax() ) * gaugeWidthMax );
                int gaugeX = outerWidth - fontRenderer.getStringWidth( value ) - 1;

                fill( x, y, x + outerWidth, y + fullHeight, 0x7F000000 );
                fill( x, y, x + 1, y + fullHeight, 0xFF00FF00 );
                fill( x + 1, y + fullHeight - 1, x + outerWidth, y + fullHeight, 0xFF00FF00 );
                fill( x + outerWidth - gaugeWidth, y, x + outerWidth, y + fullHeight, 0x7F00FF00 );
                fontRenderer.drawStringWithShadow( label, x + 2, y + 1, 0xFFFFFFFF );
                fontRenderer.drawStringWithShadow( value, x + gaugeX, y + 1, 0xFFFFFFFF );
            } );
        }
    }
}
