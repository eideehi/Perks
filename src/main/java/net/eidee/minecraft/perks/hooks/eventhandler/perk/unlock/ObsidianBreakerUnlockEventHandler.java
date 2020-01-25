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

import static net.eidee.minecraft.perks.constants.RegistryNames.PERK_OBSIDIAN_BREAKER;
import static net.eidee.minecraft.perks.perk.Perks.OBSIDIAN_BREAKER;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.network.Networks;
import net.eidee.minecraft.perks.network.message.PerkControl;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class ObsidianBreakerUnlockEventHandler
{
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void breakEvent( BlockEvent.BreakEvent event )
    {
        if ( !OBSIDIAN_BREAKER.isAvailable() || event.isCanceled() )
        {
            return;
        }
        PlayerEntity player = event.getPlayer();
        IWorld world = event.getWorld();
        if ( !world.isRemote() && player instanceof ServerPlayerEntity )
        {
            if ( event.getState().getBlock() == Blocks.OBSIDIAN )
            {
                PerkManager manager = PerkManager.of( player );
                if ( manager.progressUnlock( OBSIDIAN_BREAKER, 1.0 / OBSIDIAN_BREAKER.getLearningDifficulty() ) )
                {
                    PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with( () -> ( ServerPlayerEntity )player );
                    Networks.CHANNEL.send( target, new PerkControl.UnlockPerk( PERK_OBSIDIAN_BREAKER ) );
                }
            }
        }
    }
}
