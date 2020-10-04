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

import static net.eidee.minecraft.perks.perk.Perks.BREEDER;

import java.util.concurrent.atomic.AtomicBoolean;

import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.perk.PerkController;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class BreederPerkEventHandler
{
    private static final ThreadLocal< AtomicBoolean > lock = ThreadLocal.withInitial( AtomicBoolean::new );

    private static void breeding( BlockPos pos, PlayerEntity player, World world, Hand hand, ItemStack item )
    {
        PerkController controller = PerkManager.createController( player, BREEDER );
        if ( controller.getReferenceData().isReady() )
        {
            int n = controller.getReferenceData().getRank();
            AxisAlignedBB aabb = AxisAlignedBB.fromVector( Vector3d.copy( pos ) ).grow( n, 0, n );

            world.getEntitiesWithinAABB( AnimalEntity.class, aabb ).forEach( animal -> {
                if ( controller.canUsePerk() &&
                     !item.isEmpty() &&
                     !animal.isChild() &&
                     animal.isBreedingItem( item ) &&
                     player.interactOn( animal, hand ).isSuccessOrConsume() )
                {
                    controller.usePerk();
                }
            } );
        }
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void entityInteract( PlayerInteractEvent.EntityInteract event )
    {
        if ( !BREEDER.isAvailable() || event.isCanceled() || lock.get().get() )
        {
            return;
        }

        World world = event.getWorld();
        if ( world.isRemote() )
        {
            return;
        }

        Entity entity = event.getTarget();
        if ( !( entity instanceof AnimalEntity ) )
        {
            return;
        }

        AnimalEntity animal = ( AnimalEntity )entity;
        PlayerEntity player = event.getPlayer();

        if ( !animal.isChild() && animal.canBreed() && animal.isBreedingItem( event.getItemStack() ) )
        {
            PerkController controller = PerkManager.createController( player, BREEDER );
            if ( controller.getReferenceData().isUnlocked() )
            {
                lock.get().set( true );
                breeding( event.getPos(), player, world, event.getHand(), event.getItemStack() );
                lock.get().set( false );
            }
            else
            {
                controller.addUnlockProgress();
            }
        }
    }
}
