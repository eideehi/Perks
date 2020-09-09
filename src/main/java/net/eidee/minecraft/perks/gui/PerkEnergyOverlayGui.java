package net.eidee.minecraft.perks.gui;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.capability.Capabilities;
import net.eidee.minecraft.perks.settings.Config;
import net.eidee.minecraft.perks.util.Message;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn( Dist.CLIENT )
public class PerkEnergyOverlayGui
    extends AbstractGui
{
    public void render( MatrixStack matrixStack )
    {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayerEntity player = minecraft.player;
        if ( player == null )
        {
            return;
        }
        int x = Config.CLIENT.perkEnergyUiX.get();
        int y = Config.CLIENT.perkEnergyUiY.get();
        player.getCapability( Capabilities.PERK_ENERGY ).ifPresent( energy -> {
            FontRenderer font = minecraft.fontRenderer;
            int fontHeight = font.FONT_HEIGHT;
            int fullHeight = fontHeight + 2;

            String label = Message.format( "gui.perks.energy" );
            String max = Message.format( "gui.perks.energy.value", Integer.MAX_VALUE );
            String value = Message.format( "gui.perks.energy.value", ( int )energy.getEnergy() );
            int innerWidth = font.getStringWidth( label ) + 1 + font.getStringWidth( max );
            int outerWidth = 2 + innerWidth + 2;
            int gaugeWidthMax = font.getStringWidth( max ) + 2;
            int gaugeWidth = ( int )( ( energy.getEnergy() / energy.getMaxEnergy() ) * gaugeWidthMax );
            int gaugeX = outerWidth - font.getStringWidth( value ) - 1;

            fill( matrixStack, x, y, x + outerWidth, y + fullHeight, 0x7F000000 );
            fill( matrixStack, x, y, x + 1, y + fullHeight, 0xFF00FF00 );
            fill( matrixStack, x + 1, y + fullHeight - 1, x + outerWidth, y + fullHeight, 0xFF00FF00 );
            fill( matrixStack, x + outerWidth - gaugeWidth, y, x + outerWidth, y + fullHeight, 0x7F00FF00 );
            font.drawStringWithShadow( matrixStack, label, x + 2, y + 1, 0xFFFFFFFF );
            font.drawStringWithShadow( matrixStack, value, x + gaugeX, y + 1, 0xFFFFFFFF );
        } );
    }
}
