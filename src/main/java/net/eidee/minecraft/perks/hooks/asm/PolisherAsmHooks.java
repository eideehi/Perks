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

package net.eidee.minecraft.perks.hooks.asm;

import static net.eidee.minecraft.perks.constants.RegistryNames.PERK_POLISHER;
import static net.eidee.minecraft.perks.perk.Perks.POLISHER;

import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.network.Networks;
import net.eidee.minecraft.perks.network.message.PerkControl;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.PacketDistributor;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PolisherAsmHooks
{
    private PolisherAsmHooks()
    {
    }

    public static void grindstoneOnTake( PlayerEntity player )
    {
        if ( POLISHER.isAvailable() )
        {
            if ( !player.getEntityWorld().isRemote() && player instanceof ServerPlayerEntity )
            {
                PerkManager manager = PerkManager.of( player );
                if ( manager.progressUnlock( POLISHER, 1.0 / 512.0 ) )
                {
                    PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with( () -> ( ServerPlayerEntity )player );
                    Networks.CHANNEL.send( target, new PerkControl.UnlockPerk( PERK_POLISHER ) );
                }
            }
        }
    }

    public static boolean usePerk( PlayerEntity player, ItemStack stack )
    {
        if ( POLISHER.isAvailable() )
        {
            if ( stack.isEnchanted() )
            {
                PerkManager manager = PerkManager.of( player );
                return manager.usePerk( POLISHER, false );
            }
        }
        return false;
    }

    public static Map< Enchantment, Integer > getEnchantments( @Nullable PlayerInventory playerInventory,
                                                               ItemStack stack,
                                                               Map< Enchantment, Integer > original )
    {
        if ( POLISHER.isAvailable() )
        {
            if ( playerInventory != null )
            {
                PlayerEntity player = playerInventory.player;
                PerkManager manager = PerkManager.of( player );
                if ( manager.usePerk( POLISHER, true ) )
                {
                    return EnchantmentHelper.getEnchantments( stack );
                }
            }
        }
        return original;
    }
}
