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

package net.eidee.minecraft.perks.hooks.eventhandler.perk.effect;

import static net.eidee.minecraft.perks.perk.Perks.IRON_FIST;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Lists;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.perk.PerkData;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class IronFistEffectEventHandler
{
    private static List< Item > getIronTools( int rank )
    {
        List< Item > result = Lists.newArrayList();
        if ( rank >= 1 )
        {
            result.add( Items.IRON_SHOVEL );
        }
        if ( rank >= 2 )
        {
            result.add( Items.IRON_AXE );
        }
        if ( rank >= 3 )
        {
            result.add( Items.IRON_SWORD );
        }
        if ( rank >= 4 )
        {
            result.add( Items.IRON_PICKAXE );
        }
        return result;
    }

    @SubscribeEvent
    public static void breakSpeed( PlayerEvent.BreakSpeed event )
    {
        if ( !IRON_FIST.isAvailable() )
        {
            return;
        }
        PlayerEntity player = event.getPlayer();
        PerkManager manager = PerkManager.of( player );
        if ( manager.usePerk( IRON_FIST, true ) )
        {
            ItemStack itemMainHand = player.getHeldItemMainhand();
            if ( itemMainHand.isEmpty() || itemMainHand.getItem() instanceof BlockItem )
            {
                PerkData data = manager.getPerkStorage().getData( IRON_FIST );
                float speed = event.getNewSpeed();
                float newSpeed = speed;
                for ( Item ironTool : getIronTools( data.getRank() ) )
                {
                    ItemStack stack = new ItemStack( ironTool );
                    newSpeed = Math.max( newSpeed, stack.getDestroySpeed( event.getState() ) );
                }
                if ( speed != newSpeed && manager.usePerk( IRON_FIST, false ) )
                {
                    event.setNewSpeed( newSpeed );
                }
            }
        }
    }
}
