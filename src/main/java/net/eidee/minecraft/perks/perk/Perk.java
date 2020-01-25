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

package net.eidee.minecraft.perks.perk;

import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.registry.PerkRegistry;

import net.minecraft.util.Util;
import net.minecraftforge.registries.ForgeRegistryEntry;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class Perk
    extends ForgeRegistryEntry< Perk >
{
    public static final JsonDeserializer< Perk > DESERIALIZER = ( json, typeOfT, context ) -> {
        JsonObject object = json.getAsJsonObject();

        boolean available = object.get( "available" ).getAsBoolean();
        Rarity rarity = Rarity.valueOf( object.get( "rarity" ).getAsString() );
        int maxRank = object.get( "maxRank" ).getAsInt();
        int[] learningCosts;
        {
            JsonArray array = object.get( "learningCosts" ).getAsJsonArray();
            learningCosts = new int[ array.size() ];
            for ( int i = 0; i < learningCosts.length; i++ )
            {
                learningCosts[ i ] = array.get( i ).getAsInt();
            }
        }
        int[] usageCosts;
        {
            JsonArray array = object.get( "usageCosts" ).getAsJsonArray();
            usageCosts = new int[ array.size() ];
            for ( int i = 0; i < usageCosts.length; i++ )
            {
                usageCosts[ i ] = array.get( i ).getAsInt();
            }
        }
        int learningDifficulty = object.get( "learningDifficulty" ).getAsInt();

        return new Perk( available, rarity, maxRank, learningCosts, usageCosts, learningDifficulty );
    };

    private final boolean available;
    private final Rarity rarity;
    private final int maxRank;
    private final int[] learningCosts;
    private final int[] usageCosts;
    private final int learningDifficulty;

    protected transient String translationKey;

    private Perk( boolean available,
                  Rarity rarity,
                  int maxRank,
                  int[] learningCosts,
                  int[] usageCosts,
                  int learningDifficulty )
    {
        this.available = available;
        this.rarity = rarity;
        this.maxRank = maxRank;
        this.learningCosts = learningCosts;
        this.usageCosts = usageCosts;
        this.learningDifficulty = learningDifficulty;
    }

    protected String getTranslationKey()
    {
        if ( translationKey == null )
        {
            translationKey = Util.makeTranslationKey( "perk", PerkRegistry.getKey( this ) );
        }

        return translationKey;
    }

    public boolean isAvailable()
    {
        return available;
    }

    public Rarity getRarity()
    {
        return rarity;
    }

    public int getMaxRank()
    {
        return maxRank;
    }

    public int getLearningCost( int rank )
    {
        int index = rank - 1;
        if ( index >= 0 && index < maxRank && index < learningCosts.length )
        {
            return learningCosts[ rank - 1 ];
        }
        return -1;
    }

    public int getUsageCost( int rank )
    {
        int index = rank - 1;
        if ( index >= 0 && index < maxRank && index < usageCosts.length )
        {
            return usageCosts[ rank - 1 ];
        }
        return -1;
    }

    public int getLearningDifficulty()
    {
        return learningDifficulty;
    }

    public String getName()
    {
        return getTranslationKey();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        Perk perk = ( Perk )o;
        return Objects.equals( getRegistryName(), perk.getRegistryName() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getRegistryName() );
    }

    @Override
    public String toString()
    {
        return "Perk{" + getRegistryName() + "}";
    }

    public enum Rarity
    {
        COMMON,
        RARE,
        LEGENDARY
    }
}
