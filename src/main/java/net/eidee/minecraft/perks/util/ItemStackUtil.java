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

package net.eidee.minecraft.perks.util;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Lists;
import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemStackUtil
{
    private ItemStackUtil()
    {
    }

    public static List< ItemStack > mergeItems( List< ItemStack > items )
    {
        List< ItemStack > result = Lists.newArrayList();
outer_loop:
        for ( ItemStack stack1 : items )
        {
            for ( ItemStack stack2 : result )
            {
                if ( stack1.isEmpty() )
                {
                    continue outer_loop;
                }
                int limit = stack2.getMaxStackSize() - stack2.getCount();
                if ( limit > 0 )
                {
                    if ( ItemHandlerHelper.canItemStacksStack( stack2, stack1 ) )
                    {
                        int size = Math.min( stack1.getCount(), limit );
                        stack2.grow( size );
                        stack1.shrink( size );
                    }
                }
            }
            if ( !stack1.isEmpty() )
            {
                result.add( stack1 );
            }
        }
        return result;
    }

    public static void spawnAsEntityNoPickupDelay( World world, Vector3d pos, ItemStack stack )
    {
        if ( !world.isRemote() &&
             !stack.isEmpty() &&
             world.getGameRules().getBoolean( GameRules.DO_TILE_DROPS ) &&
             !world.restoringBlockSnapshots )
        {
            double offsetX = ( world.rand.nextFloat() * 0.5F ) + 0.25D;
            double offsetY = ( world.rand.nextFloat() * 0.5F ) + 0.25D;
            double offsetZ = ( world.rand.nextFloat() * 0.5F ) + 0.25D;
            ItemEntity itementity = new ItemEntity( world,
                                                    pos.getX() + offsetX,
                                                    pos.getY() + offsetY,
                                                    pos.getZ() + offsetZ,
                                                    stack );
            itementity.setPickupDelay( 0 );
            world.addEntity( itementity );
        }
    }
}
