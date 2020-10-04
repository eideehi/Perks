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

import static net.eidee.minecraft.perks.perk.Perks.RESIDENT_OF_END;

import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.perk.PerkController;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class ResidentOfEndPerkEventHandler
{
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void playerTickEvent( TickEvent.PlayerTickEvent event )
    {
        if ( !RESIDENT_OF_END.isAvailable() || event.phase != TickEvent.Phase.END )
        {
            return;
        }

        PlayerEntity player = event.player;
        World world = player.getEntityWorld();
        if ( !world.isRemote() )
        {
            PerkController controller = PerkManager.createController( player, RESIDENT_OF_END );
            if ( controller.getReferenceData().isUnlocked() )
            {
                return;
            }

            if ( world.getDimensionKey() == World.THE_END )
            {
                controller.addUnlockProgress();
            }
        }
    }

    @SubscribeEvent
    public static void livingAttackEvent( LivingAttackEvent event )
    {
        if ( !RESIDENT_OF_END.isAvailable() || event.isCanceled() )
        {
            return;
        }

        LivingEntity entity = event.getEntityLiving();
        World world = entity.getEntityWorld();
        if ( !world.isRemote() && entity instanceof PlayerEntity )
        {
            if ( Objects.equals( event.getSource(), DamageSource.FALL ) &&
                 entity.fallDistance == 0.0F &&
                 PerkManager.createController( ( PlayerEntity )entity, RESIDENT_OF_END ).usePerk( 3 ) )
            {
                event.setCanceled( true );
            }
        }
    }
}
