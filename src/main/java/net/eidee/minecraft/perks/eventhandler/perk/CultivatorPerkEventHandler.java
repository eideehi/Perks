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

import static net.eidee.minecraft.perks.perk.Perks.CULTIVATOR;

import java.util.concurrent.atomic.AtomicBoolean;

import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.perk.PerkController;
import net.eidee.minecraft.perks.perk.PerkManager;
import net.eidee.minecraft.perks.util.BlockPosUtil;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class CultivatorPerkEventHandler
{
    private static final ThreadLocal< AtomicBoolean > lock = ThreadLocal.withInitial( AtomicBoolean::new );

    private static void cultivate( BlockPos basePos, PlayerEntity player, World world, Hand hand, ItemStack hoe )
    {
        PerkController controller = PerkManager.createController( player, CULTIVATOR );
        if ( controller.getReferenceData().isReady() )
        {
            int n = controller.getReferenceData().getRank();
            BlockPos.Mutable upper = new BlockPos.Mutable();
            BlockPosUtil.getSpiralPositions( basePos, n ).forEachRemaining( pos -> {
                if ( !controller.canUsePerk() )
                {
                    return;
                }

                if ( world.isAirBlock( upper.setPos( pos.getX(), pos.getY() + 1, pos.getZ() ) ) &&
                     HoeItem.getHoeTillingState( world.getBlockState( pos ) ) != null )
                {
                    BlockRayTraceResult rayTrace;
                    rayTrace = new BlockRayTraceResult( Vector3d.copy( pos ), Direction.UP, pos, false );
                    ItemUseContext itemUseContext = new ItemUseContext( player, hand, rayTrace );
                    if ( hoe.onItemUse( itemUseContext ).isSuccessOrConsume() )
                    {
                        controller.usePerk();
                    }
                }
            } );
        }
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void blockToolInteract( BlockEvent.BlockToolInteractEvent event )
    {
        if ( !CULTIVATOR.isAvailable() ||
             event.isCanceled() ||
             event.getToolType() != ToolType.HOE ||
             event.getWorld().isRemote() ||
             lock.get().get() )
        {
            return;
        }

        PlayerEntity player = event.getPlayer();
        PerkController controller = PerkManager.createController( player, CULTIVATOR );

        BlockState originalState = event.getState();
        BlockState finalState = event.getFinalState();

        if ( originalState != finalState || HoeItem.getHoeTillingState( originalState ) != null )
        {
            if ( controller.getReferenceData().isUnlocked() )
            {
                ItemStack stack = event.getHeldItemStack();
                if ( stack.getItem() instanceof HoeItem )
                {
                    lock.get().set( true );
                    cultivate( event.getPos(), player, player.getEntityWorld(), player.getActiveHand(), stack );
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
