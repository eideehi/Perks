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

package net.eidee.minecraft.perks.network;

import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.network.handler.PerkControlHandler;
import net.eidee.minecraft.perks.network.handler.SyncHandler;
import net.eidee.minecraft.perks.network.message.PerkControl;
import net.eidee.minecraft.perks.network.message.Sync;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class Networks
{
    private static final ResourceLocation CHANNEL_NAME;

    private static final String PROTOCOL_VERSION;

    static
    {
        CHANNEL_NAME = new ResourceLocation( PerksMod.MOD_ID, "network/simple" );
        PROTOCOL_VERSION = "1";
    }

    public static SimpleChannel CHANNEL;

    private Networks()
    {
    }

    @SubscribeEvent
    public static void setupCommon( FMLCommonSetupEvent event )
    {
        CHANNEL = NetworkRegistry.newSimpleChannel( CHANNEL_NAME,
                                                    () -> PROTOCOL_VERSION,
                                                    PROTOCOL_VERSION::equals,
                                                    PROTOCOL_VERSION::equals );

        AtomicInteger id = new AtomicInteger( 0 );

        CHANNEL.registerMessage( id.getAndIncrement(),
                                 Sync.PerkEnergy.class,
                                 Sync.PerkEnergy::encode,
                                 Sync.PerkEnergy::decode,
                                 SyncHandler::handle );

        CHANNEL.registerMessage( id.getAndIncrement(),
                                 Sync.PerkExperience.class,
                                 Sync.PerkExperience::encode,
                                 Sync.PerkExperience::decode,
                                 SyncHandler::handle );

        CHANNEL.registerMessage( id.getAndIncrement(),
                                 Sync.PerkStorage.class,
                                 Sync.PerkStorage::encode,
                                 Sync.PerkStorage::decode,
                                 SyncHandler::handle );

        CHANNEL.registerMessage( id.getAndIncrement(),
                                 PerkControl.UnlockPerk.class,
                                 PerkControl.UnlockPerk::encode,
                                 PerkControl.UnlockPerk::decode,
                                 PerkControlHandler::handle );

        CHANNEL.registerMessage( id.getAndIncrement(),
                                 PerkControl.LearningNextRank.class,
                                 PerkControl.LearningNextRank::encode,
                                 PerkControl.LearningNextRank::decode,
                                 PerkControlHandler::handle );

        CHANNEL.registerMessage( id.getAndIncrement(),
                                 PerkControl.ChangeRank.class,
                                 PerkControl.ChangeRank::encode,
                                 PerkControl.ChangeRank::decode,
                                 PerkControlHandler::handle );

        CHANNEL.registerMessage( id.getAndIncrement(),
                                 PerkControl.SetPerkEnable.class,
                                 PerkControl.SetPerkEnable::encode,
                                 PerkControl.SetPerkEnable::decode,
                                 PerkControlHandler::handle );

        CHANNEL.registerMessage( id.getAndIncrement(),
                                 PerkControl.RemovePerk.class,
                                 PerkControl.RemovePerk::encode,
                                 PerkControl.RemovePerk::decode,
                                 PerkControlHandler::handle );
    }
}
