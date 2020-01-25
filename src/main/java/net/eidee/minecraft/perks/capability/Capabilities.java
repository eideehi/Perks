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

package net.eidee.minecraft.perks.capability;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.perk.PerkEnergy;
import net.eidee.minecraft.perks.perk.PerkExperience;
import net.eidee.minecraft.perks.perk.PerkStorage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class Capabilities
{
    @CapabilityInject( PerkEnergy.class )
    public static Capability< PerkEnergy > PERK_ENERGY;

    @CapabilityInject( PerkExperience.class )
    public static Capability< PerkExperience > PERK_EXPERIENCE;

    @CapabilityInject( PerkStorage.class )
    public static Capability< PerkStorage > PERK_STORAGE;

    private Capabilities()
    {
    }

    @SubscribeEvent
    public static void setupCommon( FMLCommonSetupEvent event )
    {
        CapabilityManager instance = CapabilityManager.INSTANCE;
        instance.register( PerkEnergy.class, BasicStorage.create( CompoundNBT.class ), PerkEnergy::new );
        instance.register( PerkExperience.class, BasicStorage.create( IntNBT.class ), PerkExperience::new );
        instance.register( PerkStorage.class, BasicStorage.create( ListNBT.class ), PerkStorage::new );
    }
}
