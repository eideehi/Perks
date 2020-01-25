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

package net.eidee.minecraft.perks.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn( Dist.CLIENT )
@Mod.EventBusSubscriber( value = { Dist.CLIENT }, modid = PerksMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class KeyBindings
{
    public static KeyBinding OPEN_PERK_GUI;

    private KeyBindings()
    {
    }

    @SubscribeEvent
    public static void setupClient( FMLClientSetupEvent event )
    {
        List< KeyBinding > keyBindings = new ArrayList<>();

        OPEN_PERK_GUI = new KeyBinding( "key.perks.open_perk_gui",
                                        KeyConflictContext.IN_GAME,
                                        InputMappings.getInputByName( "key.keyboard.p" ),
                                        "key.perks" );

        Collections.addAll( keyBindings, OPEN_PERK_GUI );
        keyBindings.forEach( ClientRegistry::registerKeyBinding );
    }
}
