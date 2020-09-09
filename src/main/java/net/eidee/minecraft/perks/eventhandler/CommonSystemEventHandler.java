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

package net.eidee.minecraft.perks.eventhandler;

import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.capability.Capabilities;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class CommonSystemEventHandler
{
    private static void syncCapabilities( PlayerEntity player )
    {
        if ( !player.getEntityWorld().isRemote() )
        {
            PerkManager.get( player ).syncCapabilities();
        }
    }

    private static < T > void mergeCapability( PlayerEntity from, PlayerEntity to, Capability< T > capability )
    {
        from.getCapability( capability ).ifPresent( fromObject -> {
            to.getCapability( capability ).ifPresent( toObject -> {
                capability.readNBT( toObject, null, capability.writeNBT( fromObject, null ) );
            } );
        } );
    }

    @SubscribeEvent( priority = EventPriority.HIGH )
    public static void playerLoggedIn( PlayerEvent.PlayerLoggedInEvent event )
    {
        syncCapabilities( event.getPlayer() );
    }

    @SubscribeEvent( priority = EventPriority.HIGH )
    public static void playerRespawn( PlayerEvent.PlayerRespawnEvent event )
    {
        syncCapabilities( event.getPlayer() );
    }

    @SubscribeEvent( priority = EventPriority.HIGH )
    public static void playerChangedDimension( PlayerEvent.PlayerChangedDimensionEvent event )
    {
        syncCapabilities( event.getPlayer() );
    }

    @SubscribeEvent( priority = EventPriority.HIGH )
    public static void clone( PlayerEvent.Clone event )
    {
        PlayerEntity clone = event.getPlayer();
        PlayerEntity original = event.getOriginal();

        original.revive();
        mergeCapability( original, clone, Capabilities.PERK_ENERGY );
        mergeCapability( original, clone, Capabilities.PERK_EXPERIENCE );
        mergeCapability( original, clone, Capabilities.PERK_DATA );
        mergeCapability( original, clone, Capabilities.PERK_EXTRA_DATA );
        original.remove( true );

        PerkManager.get( clone ).deathPenalty();
    }

    @SubscribeEvent( priority = EventPriority.HIGH )
    public static void playerTick( TickEvent.PlayerTickEvent event )
    {
        if ( event.phase == TickEvent.Phase.START )
        {
            PlayerEntity player = event.player;
            if ( !player.getEntityWorld().isRemote() )
            {
                PerkManager perkManager = PerkManager.get( player );
                perkManager.tick();
            }
        }
    }

    @SubscribeEvent
    public static void pickupXp( PlayerXpEvent.PickupXp event )
    {
        if ( !event.isCanceled() )
        {
            PlayerEntity player = event.getPlayer();
            if ( !player.getEntityWorld().isRemote() )
            {
                player.getCapability( Capabilities.PERK_EXPERIENCE ).ifPresent( experience -> {
                    experience.receiveExperience( event.getOrb().getXpValue(), false );
                } );
            }
        }
    }
}
