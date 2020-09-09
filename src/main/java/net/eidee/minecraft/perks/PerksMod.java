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

package net.eidee.minecraft.perks;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.init.CapabilityInitializer;
import net.eidee.minecraft.perks.init.CommandInitializer;
import net.eidee.minecraft.perks.init.NetworkInitializer;
import net.eidee.minecraft.perks.registry.PerkExtraDataRegistry;
import net.eidee.minecraft.perks.settings.Config;
import net.eidee.minecraft.perks.settings.KeyBindings;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod( PerksMod.MOD_ID )
public class PerksMod
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "perks";

    public PerksMod()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener( this::setup );
        FMLJavaModLoadingContext.get().getModEventBus().addListener( this::clientSetup );
        FMLJavaModLoadingContext.get().getModEventBus().addListener( this::loadComplete );
        MinecraftForge.EVENT_BUS.addListener( this::serverAboutToStart );
        ModLoadingContext.get().registerConfig( ModConfig.Type.CLIENT, Config.CLIENT_SPEC );
    }

    public static Logger getLogger()
    {
        return LOGGER;
    }

    private void setup( FMLCommonSetupEvent event )
    {
        CapabilityInitializer.registerCapability();
        NetworkInitializer.registerMessage();
        PerkExtraDataRegistry.registerPerkExtraData();
    }

    private void clientSetup( FMLClientSetupEvent event )
    {
        KeyBindings.getAll().forEach( ClientRegistry::registerKeyBinding );
    }

    private void loadComplete( FMLLoadCompleteEvent event )
    {
        PerkExtraDataRegistry.lock();
    }

    private void serverAboutToStart( FMLServerAboutToStartEvent event )
    {
        CommandInitializer.registerCommand( event.getServer().getCommandManager().getDispatcher() );
    }
}
