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

package net.eidee.minecraft.perks.hooks.eventhandler.system;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.capability.Capabilities;
import net.eidee.minecraft.perks.network.Networks;
import net.eidee.minecraft.perks.network.message.Sync;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class PerkSystemEventHandler
{
    private static void syncCapabilities( PlayerEntity player )
    {
        if ( !player.getEntityWorld().isRemote() && player instanceof ServerPlayerEntity )
        {
            PerkManager manager = PerkManager.of( player );
            PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with( () -> ( ServerPlayerEntity )player );

            Networks.CHANNEL.send( target, new Sync.PerkEnergy( manager.getPerkEnergy().serializeNBT() ) );
            Networks.CHANNEL.send( target, new Sync.PerkExperience( manager.getPerkExperience().serializeNBT() ) );
            Networks.CHANNEL.send( target, new Sync.PerkStorage( manager.getPerkStorage().serializeNBT() ) );
        }
    }

    private static < NBT extends INBT, TYPE extends INBTSerializable< NBT > > void mergeCapability( PlayerEntity from,
                                                                                                    PlayerEntity to,
                                                                                                    Capability< TYPE > capability )
    {
        from.getCapability( capability ).ifPresent( fromObject -> {
            to.getCapability( capability ).ifPresent( toObject -> {
                toObject.deserializeNBT( fromObject.serializeNBT() );
            } );
        } );
    }

    @SubscribeEvent( priority = EventPriority.HIGH )
    public static void clone( PlayerEvent.Clone event )
    {
        PlayerEntity clone = event.getPlayer();
        PlayerEntity original = event.getOriginal();
        original.revive();
        mergeCapability( original, clone, Capabilities.PERK_STORAGE );
        mergeCapability( original, clone, Capabilities.PERK_ENERGY );
        original.remove( true );

        PerkManager.of( clone ).resetPerkEnergy();
    }

    @SubscribeEvent( priority = EventPriority.HIGH )
    public static void playerTickEvent( TickEvent.PlayerTickEvent event )
    {
        if ( event.phase == TickEvent.Phase.START )
        {
            PlayerEntity player = event.player;
            if ( !player.getEntityWorld().isRemote() && player instanceof ServerPlayerEntity )
            {
                PerkManager manager = PerkManager.of( player );
                manager.tick();
                if ( manager.isPerkEnergyDirty() )
                {
                    PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with( () -> ( ServerPlayerEntity )player );
                    Networks.CHANNEL.send( target, new Sync.PerkEnergy( manager.getPerkEnergy().serializeNBT() ) );

                    manager.clearPerkEnergyDirty();
                }
            }
        }
    }

    @SubscribeEvent( priority = EventPriority.HIGH )
    public static void playerLoggedInEvent( PlayerEvent.PlayerLoggedInEvent event )
    {
        syncCapabilities( event.getPlayer() );
    }

    @SubscribeEvent( priority = EventPriority.HIGH )
    public static void playerRespawnEvent( PlayerEvent.PlayerRespawnEvent event )
    {
        syncCapabilities( event.getPlayer() );
    }

    @SubscribeEvent( priority = EventPriority.HIGH )
    public static void playerChangedDimensionEvent( PlayerEvent.PlayerChangedDimensionEvent event )
    {
        syncCapabilities( event.getPlayer() );
    }
}
