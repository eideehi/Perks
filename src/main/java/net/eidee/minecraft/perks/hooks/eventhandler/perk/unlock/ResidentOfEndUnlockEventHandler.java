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

package net.eidee.minecraft.perks.hooks.eventhandler.perk.unlock;

import static net.eidee.minecraft.perks.constants.RegistryNames.PERK_RESIDENT_OF_END;
import static net.eidee.minecraft.perks.perk.Perks.RESIDENT_OF_END;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.network.Networks;
import net.eidee.minecraft.perks.network.message.PerkControl;
import net.eidee.minecraft.perks.perk.PerkData;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.dimension.EndDimension;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class ResidentOfEndUnlockEventHandler
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
        if ( !world.isRemote() && player instanceof ServerPlayerEntity )
        {
            PerkManager manager = PerkManager.of( player );
            PerkData data = manager.getPerkStorage().getData( RESIDENT_OF_END );
            if ( data.isLearned() )
            {
                return;
            }
            if ( world.getDimension() instanceof EndDimension )
            {
                if ( manager.progressUnlock( RESIDENT_OF_END, 1.0 / RESIDENT_OF_END.getLearningDifficulty() ) )
                {
                    PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with( () -> ( ServerPlayerEntity )player );
                    Networks.CHANNEL.send( target, new PerkControl.UnlockPerk( PERK_RESIDENT_OF_END ) );
                }
            }
        }
    }
}