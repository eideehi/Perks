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

import static net.eidee.minecraft.perks.perk.Perks.SPREADER;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.perk.PerkController;
import net.eidee.minecraft.perks.perk.PerkManager;
import net.eidee.minecraft.perks.util.BlockPosUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class SpreaderPerkEventHandler
{
    private static final ThreadLocal< AtomicBoolean > lock = ThreadLocal.withInitial( AtomicBoolean::new );

    private static boolean check( World world, BlockPos pos, BlockState state )
    {
        Block block = state.getBlock();
        if ( block instanceof CropsBlock )
        {
            CropsBlock crops = ( CropsBlock )block;
            return crops.canGrow( world, pos, state, false ) && crops.canUseBonemeal( world, world.rand, pos, state );
        }
        else if ( block instanceof StemBlock )
        {
            StemBlock stem = ( StemBlock )block;
            return stem.canGrow( world, pos, state, false ) && stem.canUseBonemeal( world, world.rand, pos, state );
        }
        else
        {
            return false;
        }
    }

    private static void spread( BlockPos basePos, PlayerEntity player, World world, Hand hand, ItemStack bonemeal )
    {
        PerkController controller = PerkManager.createController( player, SPREADER );
        if ( controller.getReferenceData().isReady() )
        {
            int n = controller.getReferenceData().getRank();
            BlockPosUtil.getSpiralPositions( basePos, n ).forEachRemaining( pos -> {
                if ( !controller.canUsePerk() || bonemeal.isEmpty() )
                {
                    return;
                }

                if ( check( world, pos, world.getBlockState( pos ) ) )
                {
                    BlockRayTraceResult rayTrace;
                    rayTrace = new BlockRayTraceResult( Vector3d.copy( pos ), Direction.UP, pos, false );
                    ItemUseContext itemUseContext = new ItemUseContext( player, hand, rayTrace );
                    if ( bonemeal.onItemUse( itemUseContext ).isSuccessOrConsume() )
                    {
                        controller.usePerk();
                    }
                }
            } );
        }
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void bonemealEvent( BonemealEvent event )
    {
        if ( !SPREADER.isAvailable() ||
             event.isCanceled() ||
             event.getResult() == Event.Result.ALLOW ||
             lock.get().get() )
        {
            return;
        }

        PlayerEntity player = event.getPlayer();
        World world = event.getWorld();
        if ( world.isRemote() )
        {
            return;
        }

        BlockPos pos = event.getPos();
        if ( check( world, pos, event.getBlock() ) )
        {
            PerkController controller = PerkManager.createController( player, SPREADER );
            if ( controller.getReferenceData().isUnlocked() )
            {
                Hand hand = player.getActiveHand();
                ItemStack stack = player.getHeldItem( hand );
                if ( stack.getItem() instanceof BoneMealItem )
                {
                    lock.get().set( true );
                    spread( pos, player, world, hand, stack );
                    lock.get().set( false );
                }
            }
            else
            {
                controller.addUnlockProgress();
            }
        }
    }
}
