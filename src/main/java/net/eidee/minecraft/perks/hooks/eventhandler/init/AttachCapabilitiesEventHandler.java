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

package net.eidee.minecraft.perks.hooks.eventhandler.init;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.capability.BasicProvider;
import net.eidee.minecraft.perks.capability.Capabilities;
import net.eidee.minecraft.perks.constants.RegistryKeys;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class AttachCapabilitiesEventHandler
{
    @SubscribeEvent
    public static void attachForEntity( AttachCapabilitiesEvent< Entity > event )
    {
        Entity object = event.getObject();
        if ( object instanceof PlayerEntity )
        {
            event.addCapability( RegistryKeys.PERK_ENERGY,
                                 BasicProvider.create( Capabilities.PERK_ENERGY, CompoundNBT.class ) );
            event.addCapability( RegistryKeys.PERK_EXPERIENCE,
                                 BasicProvider.create( Capabilities.PERK_EXPERIENCE, IntNBT.class ) );
            event.addCapability( RegistryKeys.PERK_STORAGE,
                                 BasicProvider.create( Capabilities.PERK_STORAGE, ListNBT.class ) );
        }
    }

    @SubscribeEvent
    public static void attachForChunk( AttachCapabilitiesEvent< Chunk > event )
    {
        //event.addCapability( RegistryKeys.DIRTY_POSITION_LIST,
        //                     BasicProvider.create( Capabilities.DIRTY_POSITION_LIST, ListNBT.class ) );
    }
}
