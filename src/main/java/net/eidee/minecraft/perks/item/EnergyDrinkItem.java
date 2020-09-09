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

package net.eidee.minecraft.perks.item;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EnergyDrinkItem
    extends Item
{
    private Type type;
    private int value;

    public EnergyDrinkItem( Properties properties )
    {
        super( properties );
    }

    public EnergyDrinkItem setType( Type type )
    {
        this.type = type;
        return this;
    }

    public EnergyDrinkItem setValue( int value )
    {
        this.value = value;
        return this;
    }

    public int getValue()
    {
        return value;
    }

    @Override
    public ItemStack onItemUseFinish( ItemStack stack, World worldIn, LivingEntity entityLiving )
    {
        if ( entityLiving instanceof PlayerEntity )
        {
            PlayerEntity player = ( PlayerEntity )entityLiving;
            if ( player instanceof ServerPlayerEntity )
            {
                CriteriaTriggers.CONSUME_ITEM.trigger( ( ServerPlayerEntity )player, stack );
            }

            if ( type == Type.RECOVER )
            {
                PerkManager.get( player ).recoverEnergy( value );
            }
            else
            {
                PerkManager.get( player ).boostEnergy( getTranslationKey(), value, true, 36000 );
            }

            player.addStat( Stats.ITEM_USED.get( this ) );
            if ( !player.abilities.isCreativeMode )
            {
                stack.shrink( 1 );
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration( ItemStack stack )
    {
        return 32;
    }

    @Override
    public UseAction getUseAction( ItemStack stack )
    {
        return UseAction.DRINK;
    }

    @Override
    public ActionResult< ItemStack > onItemRightClick( World worldIn, PlayerEntity playerIn, Hand handIn )
    {
        return DrinkHelper.func_234707_a_( worldIn, playerIn, handIn );
    }

    public enum Type
    {
        RECOVER,
        BOOST
    }
}
