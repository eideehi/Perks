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

package net.eidee.minecraft.perks.hooks.asm;

import static net.eidee.minecraft.perks.perk.Perks.RESIDENT_OF_END;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.Mod;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID )
public class ResidentOfEndAsmHooks
{
    private ResidentOfEndAsmHooks()
    {
    }

    public static boolean suppressTeleport( LivingEntity livingEntity )
    {
        if ( RESIDENT_OF_END.isAvailable() )
        {
            if ( livingEntity instanceof PlayerEntity )
            {
                PerkManager manager = PerkManager.of( ( PlayerEntity )livingEntity );
                return manager.usePerk( RESIDENT_OF_END, 1, false );
            }
        }
        return false;
    }

    public static boolean suppressEndermiteSpawn( PlayerEntity player )
    {
        if ( RESIDENT_OF_END.isAvailable() )
        {
            PerkManager manager = PerkManager.of( player );
            return manager.usePerk( RESIDENT_OF_END, 2, false );
        }
        return false;
    }
}
