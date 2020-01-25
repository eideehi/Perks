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

import static net.eidee.minecraft.perks.perk.Perks.CULTIVATOR;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.perk.PerkData;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class CultivatorEffectEventHandler
{
    private static void cultivate( PlayerEntity player, World world, Hand hand, ItemStack hoe )
    {
        PerkManager manager = PerkManager.of( player );
        PerkData data = manager.getPerkStorage().getData( CULTIVATOR );
        if ( data.isLearned() && data.isEnabled() )
        {
            int n = data.getRank() - 1;
            BlockPos pos = player.getPosition();
            int xBase = pos.getX();
            int yBase = pos.getY();
            int zBase = pos.getZ();
            BlockPos.Mutable upper = new BlockPos.Mutable();
            BlockPos.Mutable lower = new BlockPos.Mutable();
            for ( int x = xBase - n; x <= xBase + n; x++ )
            {
                for ( int z = zBase - n; z <= zBase + n; z++ )
                {
                    for ( int y = yBase + 1; y >= yBase; y-- )
                    {
                        if ( !manager.usePerk( CULTIVATOR, true ) )
                        {
                            return;
                        }
                        upper.setPos( x, y, z );
                        lower.setPos( x, y - 1, z );
                        if ( world.isAirBlock( upper ) &&
                             HoeItem.HOE_LOOKUP.containsKey( world.getBlockState( lower ).getBlock() ) )
                        {
                            BlockRayTraceResult rayTrace;
                            rayTrace = new BlockRayTraceResult( new Vec3d( lower ), Direction.UP, lower, false );
                            ItemUseContext itemUseContext = new ItemUseContext( player, hand, rayTrace );
                            if ( hoe.onItemUse( itemUseContext ) == ActionResultType.SUCCESS )
                            {
                                manager.usePerk( CULTIVATOR, false );
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerTickEvent( TickEvent.PlayerTickEvent event )
    {
        if ( !CULTIVATOR.isAvailable() || event.phase != TickEvent.Phase.END )
        {
            return;
        }
        PlayerEntity player = event.player;
        World world = player.getEntityWorld();
        if ( !world.isRemote() )
        {
            for ( Hand hand : Hand.values() )
            {
                ItemStack stack = player.getHeldItem( hand );
                if ( stack.getItem() instanceof HoeItem )
                {
                    cultivate( player, world, hand, stack );
                }
            }
        }
    }
}
