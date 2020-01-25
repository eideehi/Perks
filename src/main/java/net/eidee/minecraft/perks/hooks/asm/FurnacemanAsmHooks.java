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

import static net.eidee.minecraft.perks.perk.Perks.FURNACEMAN;

import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.perk.PerkData;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FurnacemanAsmHooks
{
    private FurnacemanAsmHooks()
    {
    }

    private static int getExtraCookTime( int rank )
    {
        switch ( rank )
        {
            default:
            case 1:
                return 1;

            case 2:
                return 2;

            case 3:
                return 3;
        }
    }

    public static int calcCookTime( TileEntity furnace, int cookTime, int cookTimeTotal )
    {
        if ( FURNACEMAN.isAvailable() )
        {
            World world = Objects.requireNonNull( furnace.getWorld() );
            BlockPos pos = furnace.getPos();
            PlayerEntity player = world.getClosestPlayer( pos.getX(), pos.getY(), pos.getZ(), 2.0, entity -> {
                if ( entity instanceof PlayerEntity )
                {
                    PerkManager manager = PerkManager.of( ( PlayerEntity )entity );
                    return manager.usePerk( FURNACEMAN, true );
                }
                return false;
            } );
            if ( player != null )
            {
                PerkManager manager = PerkManager.of( player );
                if ( manager.usePerk( FURNACEMAN, false ) )
                {
                    PerkData data = manager.getPerkStorage().getData( FURNACEMAN );
                    int extraCookTime = getExtraCookTime( data.getRank() );
                    return Math.min( cookTime + extraCookTime, cookTimeTotal );
                }
            }
        }
        return cookTime;
    }
}
