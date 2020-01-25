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

import static net.eidee.minecraft.perks.perk.Perks.FEEDER;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.perk.PerkData;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class FeederEffectEventHandler
{
    private static void feeding( PlayerEntity player, World world, Hand hand )
    {
        PerkManager manager = PerkManager.of( player );
        PerkData data = manager.getPerkStorage().getData( FEEDER );
        if ( data.isLearned() && data.isEnabled() )
        {
            ItemStack heldItem = player.getHeldItem( hand );
            int n = data.getRank();
            AxisAlignedBB axisAlignedBB = player.getBoundingBox().grow( n, 0, n );
            world.getEntitiesWithinAABB( AnimalEntity.class, axisAlignedBB ).forEach( animal -> {
                if ( manager.usePerk( FEEDER, true ) )
                {
                    if ( animal.isChild() && animal.isBreedingItem( heldItem ) )
                    {
                        if ( player.interactOn( animal, hand ) == ActionResultType.SUCCESS )
                        {
                            manager.usePerk( FEEDER, false );
                        }
                    }
                }
            } );
        }
    }

    @SubscribeEvent
    public static void playerTickEvent( TickEvent.PlayerTickEvent event )
    {
        if ( !FEEDER.isAvailable() || event.phase != TickEvent.Phase.END )
        {
            return;
        }
        PlayerEntity player = event.player;
        for ( Hand hand : Hand.values() )
        {
            feeding( player, player.getEntityWorld(), hand );
        }
    }
}
