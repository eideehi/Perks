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

package net.eidee.minecraft.perks.network.handler;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.gui.toasts.PerkUnlockToast;
import net.eidee.minecraft.perks.network.Networks;
import net.eidee.minecraft.perks.network.message.PerkControl;
import net.eidee.minecraft.perks.network.message.Sync;
import net.eidee.minecraft.perks.perk.PerkManager;
import net.eidee.minecraft.perks.registry.PerkRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkControlHandler
{
    public static void handle( PerkControl.UnlockPerk message, Supplier< NetworkEvent.Context > ctxSupplier )
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork( () -> {
            PerkRegistry.getValue( message.getPerkId() ).ifPresent( perk -> {
                Minecraft minecraft = Minecraft.getInstance();
                ClientPlayerEntity player = Objects.requireNonNull( minecraft.player );
                if ( PerkManager.of( player ).progressUnlock( perk, 1.0 ) )
                {
                    minecraft.getToastGui().add( new PerkUnlockToast( perk ) );
                }
            } );
        } );
        ctx.setPacketHandled( true );
    }

    public static void handle( PerkControl.LearningNextRank message, Supplier< NetworkEvent.Context > ctxSupplier )
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork( () -> {
            PerkRegistry.getValue( message.getPerkId() ).ifPresent( perk -> {
                Optional.ofNullable( ctx.getSender() ).ifPresent( player -> {
                    PerkManager manager = PerkManager.of( player );
                    if ( manager.learningNextRank( perk ) )
                    {
                        PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with( () -> player );
                        Networks.CHANNEL.send( target, new Sync.PerkExperience( manager.getPerkExperience().serializeNBT() ) );
                        Networks.CHANNEL.send( target, new Sync.PerkStorage( manager.getPerkStorage().serializeNBT() ) );
                    }
                } );
            } );
        } );
        ctx.setPacketHandled( true );
    }

    public static void handle( PerkControl.ChangeRank message, Supplier< NetworkEvent.Context > ctxSupplier )
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork( () -> {
            PerkRegistry.getValue( message.getPerkId() ).ifPresent( perk -> {
                Optional.ofNullable( ctx.getSender() ).ifPresent( player -> {
                    PerkManager manager = PerkManager.of( player );
                    if ( manager.changeRank( perk, message.getNewRank() ) )
                    {
                        PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with( () -> player );
                        Networks.CHANNEL.send( target, new Sync.PerkStorage( manager.getPerkStorage().serializeNBT() ) );
                    }
                } );
            } );
        } );
        ctx.setPacketHandled( true );
    }

    public static void handle( PerkControl.SetPerkEnable message, Supplier< NetworkEvent.Context > ctxSupplier )
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork( () -> {
            PerkRegistry.getValue( message.getPerkId() ).ifPresent( perk -> {
                Optional.ofNullable( ctx.getSender() ).ifPresent( player -> {
                    PerkManager manager = PerkManager.of( player );
                    if ( manager.setPerkEnable( perk, message.isEnable() ) )
                    {
                        PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with( () -> player );
                        Networks.CHANNEL.send( target, new Sync.PerkStorage( manager.getPerkStorage().serializeNBT() ) );
                    }
                } );
            } );
        } );
        ctx.setPacketHandled( true );
    }

    public static void handle( PerkControl.RemovePerk message, Supplier< NetworkEvent.Context > ctxSupplier )
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork( () -> {
            PerkRegistry.getValue( message.getPerkId() ).ifPresent( perk -> {
                Minecraft minecraft = Minecraft.getInstance();
                ClientPlayerEntity player = Objects.requireNonNull( minecraft.player );
                PerkManager.of( player ).removePerk( perk );
            } );
        } );
        ctx.setPacketHandled( true );
    }
}
