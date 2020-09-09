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

import static net.eidee.minecraft.perks.perk.Perks.SEEDER;

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
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class SeederPerkEventHandler
{
    private static final ThreadLocal< AtomicBoolean > lock = ThreadLocal.withInitial( AtomicBoolean::new );

    private static void sow( BlockPos basePos, PlayerEntity player, World world, Hand hand, ItemStack seed )
    {
        PerkController controller = PerkManager.createController( player, SEEDER );
        if ( controller.getReferenceData().isReady() )
        {
            int n = controller.getReferenceData().getRank();
            BlockPos.Mutable lower = new BlockPos.Mutable();
            BlockPosUtil.getSpiralPositions( basePos, n ).forEachRemaining( pos -> {
                if ( !controller.canUsePerk() || seed.isEmpty() )
                {
                    return;
                }

                lower.setPos( pos.getX(), pos.getY() - 1, pos.getZ() );
                if ( world.isAirBlock( pos ) && world.getBlockState( lower ).getBlock() instanceof FarmlandBlock )
                {
                    BlockRayTraceResult rayTrace;
                    rayTrace = new BlockRayTraceResult( Vector3d.copy( lower ), Direction.UP, lower, false );
                    ItemUseContext itemUseContext = new ItemUseContext( player, hand, rayTrace );
                    if ( seed.onItemUse( itemUseContext ).isSuccessOrConsume() )
                    {
                        controller.usePerk();
                    }
                }
            } );
        }
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void entityPlaceEvent( BlockEvent.EntityPlaceEvent event )
    {
        if ( !SEEDER.isAvailable() || event.isCanceled() || event.getWorld().isRemote() || lock.get().get() )
        {
            return;
        }

        Entity entity = event.getEntity();
        if ( !( entity instanceof PlayerEntity ) )
        {
            return;
        }

        PlayerEntity player = ( PlayerEntity )entity;
        PerkController controller = PerkManager.createController( player, SEEDER );

        BlockState state = event.getPlacedBlock();
        Block block = state.getBlock();

        if ( block instanceof CropsBlock || block instanceof StemBlock )
        {
            if ( controller.getReferenceData().isUnlocked() )
            {
                if ( block instanceof StemBlock )
                {
                    // StemBlock is out of scope until we come up with a good seeding treatment.
                    return;
                }

                Hand hand = player.getActiveHand();
                ItemStack heldItem = player.getHeldItem( hand );
                Item item = heldItem.getItem();
                if ( item instanceof BlockItem && ( ( BlockItem )item ).getBlock() == block )
                {
                    lock.get().set( true );
                    sow( event.getPos(), player, player.getEntityWorld(), hand, heldItem );
                    // The count of used items rolls back, so we set a new held item.
                    player.setHeldItem( hand, heldItem.copy() );
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
