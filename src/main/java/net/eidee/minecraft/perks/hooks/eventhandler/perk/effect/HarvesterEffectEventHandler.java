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

import static net.eidee.minecraft.perks.perk.Perks.HARVESTER;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.perk.PerkData;
import net.eidee.minecraft.perks.perk.PerkManager;
import net.eidee.minecraft.perks.util.ItemStackUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class HarvesterEffectEventHandler
{
    private static void harvest( PlayerEntity player, ServerWorld world )
    {
        PerkManager manager = PerkManager.of( player );
        PerkData data = manager.getPerkStorage().getData( HARVESTER );
        if ( data.isLearned() && data.isEnabled() )
        {
            List< ItemStack > drops = new ArrayList<>();
            ItemStack itemMainHand = player.getHeldItemMainhand();

            int n = data.getRank() - 1;
            BlockPos pos = player.getPosition();
            int xBase = pos.getX();
            int yBase = pos.getY();
            int zBase = pos.getZ();
            BlockPos.Mutable target = new BlockPos.Mutable();
            for ( int x = xBase - n; x <= xBase + n; x++ )
            {
                for ( int z = zBase - n; z <= zBase + n; z++ )
                {
                    for ( int y = yBase + 1; y >= yBase; y-- )
                    {
                        if ( !manager.usePerk( HARVESTER, true ) )
                        {
                            return;
                        }
                        target.setPos( x, y, z );
                        BlockState state = world.getBlockState( target );
                        Block block = state.getBlock();
                        if ( block instanceof CropsBlock )
                        {
                            CropsBlock crops = ( CropsBlock )block;
                            if ( state.get( crops.getAgeProperty() ) == crops.getMaxAge() )
                            {
                                manager.usePerk( HARVESTER, false );

                                drops.addAll( Block.getDrops( state,
                                                              world,
                                                              pos,
                                                              world.getTileEntity( target ),
                                                              player,
                                                              itemMainHand ) );
                                world.destroyBlock( target, false );
                            }
                            break;
                        }
                    }
                }
            }

            ItemStackUtil.mergeItems(drops).forEach( item -> {
                ItemStackUtil.spawnAsEntityNoPickupDelay( world, player.getPositionVec(), item );
            } );
        }
    }

    @SubscribeEvent
    public static void playerTickEvent( TickEvent.PlayerTickEvent event )
    {
        if ( !HARVESTER.isAvailable() || event.phase != TickEvent.Phase.END )
        {
            return;
        }
        PlayerEntity player = event.player;
        World world = player.getEntityWorld();
        if ( !world.isRemote() && world instanceof ServerWorld )
        {
            harvest( player, ( ServerWorld )world );
        }
    }
}
