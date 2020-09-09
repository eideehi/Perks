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

package net.eidee.minecraft.perks.gui.toasts;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.perk.Perk;
import net.eidee.minecraft.perks.util.Message;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn( Dist.CLIENT )
public class PerkUnlockToast
    implements IToast
{
    private String perkTranslationKey;
    private long firstDrawTime;
    private boolean newDisplay;

    public PerkUnlockToast( Perk perk )
    {
        this.perkTranslationKey = perk.getName();
    }

    @Override
    public Visibility func_230444_a_( MatrixStack matrixStack, ToastGui toastGui, long delta )
    {
        if ( newDisplay )
        {
            firstDrawTime = delta;
            newDisplay = false;
        }

        Minecraft minecraft = toastGui.getMinecraft();
        minecraft.getTextureManager().bindTexture( TEXTURE_TOASTS );
        RenderSystem.color3f( 1.0F, 1.0F, 1.0F );
        toastGui.blit( matrixStack, 0, 0, 0, 64, 160, 32 );
        minecraft.fontRenderer.drawString( matrixStack,
                                           Message.format( "gui.perks.toast.unlock.text" ),
                                           30.0F,
                                           7.0F,
                                           0xFFFFFFFF );
        minecraft.fontRenderer.drawString( matrixStack,
                                           Message.format( perkTranslationKey ),
                                           30.0F,
                                           18.0F,
                                           0xFFFFFFFF );

        return delta - firstDrawTime < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
    }
}
