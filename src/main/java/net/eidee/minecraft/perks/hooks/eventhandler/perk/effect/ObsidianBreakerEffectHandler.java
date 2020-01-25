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

package net.eidee.minecraft.perks.hooks.eventhandler.perk.effect;

import static net.eidee.minecraft.perks.perk.Perks.OBSIDIAN_BREAKER;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.perk.PerkData;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class ObsidianBreakerEffectHandler
{
    private static int getObsidianBreakerMultiplier( int rank )
    {
        switch ( rank )
        {
            default:
            case 1:
                return 2;

            case 2:
                return 3;

            case 3:
                return 4;
        }
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void breakSpeed( PlayerEvent.BreakSpeed event )
    {
        if ( !OBSIDIAN_BREAKER.isAvailable() || event.isCanceled() )
        {
            return;
        }
        if ( event.getState().getBlock() == Blocks.OBSIDIAN )
        {
            PlayerEntity player = event.getPlayer();
            PerkManager manager = PerkManager.of( player );
            if ( manager.usePerk( OBSIDIAN_BREAKER, false ) )
            {
                PerkData data = manager.getPerkStorage().getData( OBSIDIAN_BREAKER );
                int multiplier = getObsidianBreakerMultiplier( data.getRank() );
                event.setNewSpeed( event.getNewSpeed() * multiplier );
            }
        }
    }
}
