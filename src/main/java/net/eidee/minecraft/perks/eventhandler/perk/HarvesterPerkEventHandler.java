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

package net.eidee.minecraft.perks.eventhandler.perk;

import static net.eidee.minecraft.perks.perk.Perks.HARVESTER;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.perk.PerkController;
import net.eidee.minecraft.perks.perk.PerkManager;
import net.eidee.minecraft.perks.util.BlockPosUtil;
import net.eidee.minecraft.perks.util.ItemStackUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.StemGrownBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class HarvesterPerkEventHandler
{
    private static final ThreadLocal< AtomicBoolean > lock = ThreadLocal.withInitial( AtomicBoolean::new );

    private static boolean check( World world, BlockPos pos, BlockState state )
    {
        Block block = state.getBlock();
        if ( block instanceof CropsBlock )
        {
            return !( ( CropsBlock )block ).canGrow( world, pos, state, false );
        }
        else
        {
            return block instanceof StemGrownBlock;
        }
    }

    private static void harvest( BlockPos basePos, PlayerEntity player, BlockState state1 )
    {
        World entityWorld = player.getEntityWorld();
        if ( !( entityWorld instanceof ServerWorld ) )
        {
            return;
        }

        ServerWorld world = ( ServerWorld )entityWorld;

        PerkController controller = PerkManager.createController( player, HARVESTER );
        if ( controller.getReferenceData().isReady() )
        {
            Block sample = state1.getBlock();

            List< ItemStack > drops = new ArrayList<>();
            ItemStack item = player.getHeldItemMainhand();

            int n = controller.getReferenceData().getRank();
            BlockPosUtil.getSpiralPositions( basePos, n ).forEachRemaining( pos -> {
                if ( !controller.canUsePerk() )
                {
                    return;
                }

                BlockState state = world.getBlockState( pos );
                if ( state.getBlock() == sample && check( world, pos, state ) )
                {
                    controller.usePerk();
                    drops.addAll( Block.getDrops( state, world, basePos, world.getTileEntity( pos ), player, item ) );
                    world.destroyBlock( pos, false );
                }
            } );

            ItemStackUtil.mergeItems( drops ).forEach( drop -> {
                ItemStackUtil.spawnAsEntityNoPickupDelay( world, player.getPositionVec(), drop );
            } );
        }
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void breakEvent( BlockEvent.BreakEvent event )
    {
        if ( !HARVESTER.isAvailable() || event.isCanceled() || lock.get().get() )
        {
            return;
        }

        IWorld world = event.getWorld();
        PlayerEntity player = event.getPlayer();
        if ( world.isRemote() )
        {
            return;
        }

        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        if ( check( player.getEntityWorld(), pos, state ) )
        {
            PerkController controller = PerkManager.createController( player, HARVESTER );
            if ( controller.getReferenceData().isUnlocked() )
            {
                lock.get().set( true );
                harvest( pos, player, state );
                lock.get().set( false );
            }
            else
            {
                controller.addUnlockProgress();
            }
        }
    }
}
