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
    public ItemStack onItemUseFinish( ItemStack p_77654_1_, World p_77654_2_, LivingEntity p_77654_3_ )
    {
        if ( p_77654_3_ instanceof PlayerEntity )
        {
            PlayerEntity player = ( PlayerEntity )p_77654_3_;
            if ( player instanceof ServerPlayerEntity )
            {
                CriteriaTriggers.CONSUME_ITEM.trigger( ( ServerPlayerEntity )player, p_77654_1_ );
            }

            if ( type == Type.RECOVER )
            {
                PerkManager.of( player ).recoverPerkEnergy( value );
            }
            else
            {
                PerkManager.of( player ).addPerkEnergyBase( false, value );
            }

            player.addStat( Stats.ITEM_USED.get( this ) );
            if ( !player.abilities.isCreativeMode )
            {
                p_77654_1_.shrink( 1 );
            }
        }
        return p_77654_1_;
    }

    @Override
    public int getUseDuration( ItemStack p_77626_1_ )
    {
        return 32;
    }

    @Override
    public UseAction getUseAction( ItemStack p_77661_1_ )
    {
        return UseAction.DRINK;
    }

    @Override
    public ActionResult< ItemStack > onItemRightClick( World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_ )
    {
        p_77659_2_.setActiveHand( p_77659_3_ );
        return ActionResult.func_226248_a_( p_77659_2_.getHeldItem( p_77659_3_ ) );
    }

    public enum Type
    {
        RECOVER,
        BASE_UP
    }
}
