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

package net.eidee.minecraft.perks.asm;

import static net.eidee.minecraft.perks.perk.Perks.POLISHER;

import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PolisherHooks
{
    private PolisherHooks()
    {
    }

    public static void grindstoneOnTake( PlayerEntity player )
    {
        if ( POLISHER.isAvailable() && !player.getEntityWorld().isRemote() )
        {
            PerkManager.createController( player, POLISHER ).addUnlockProgress();
        }
    }

    public static boolean usePerk( PlayerEntity player, ItemStack stack )
    {
        if ( POLISHER.isAvailable() && stack.isEnchanted() )
        {
            return PerkManager.createController( player, POLISHER ).usePerk();
        }
        return false;
    }

    public static Map< Enchantment, Integer > getEnchantments( @Nullable PlayerInventory playerInventory,
                                                               ItemStack stack,
                                                               Map< Enchantment, Integer > original )
    {
        if ( POLISHER.isAvailable() && playerInventory != null )
        {
            if ( PerkManager.createController( playerInventory.player, POLISHER ).canUsePerk() )
            {
                return EnchantmentHelper.getEnchantments( stack );
            }
        }
        return original;
    }
}
