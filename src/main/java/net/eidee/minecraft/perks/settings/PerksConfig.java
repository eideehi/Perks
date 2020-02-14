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

package net.eidee.minecraft.perks.settings;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public class PerksConfig
{
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static
    {
        Pair< Client, ForgeConfigSpec > configure = new ForgeConfigSpec.Builder().configure( Client::new );
        CLIENT_SPEC = configure.getRight();
        CLIENT = configure.getLeft();
    }

    public static class Client
    {
        public final ForgeConfigSpec.IntValue perkEnergyUiX;
        public final ForgeConfigSpec.IntValue perkEnergyUiY;

        public Client( ForgeConfigSpec.Builder builder )
        {
            builder.comment( "" ).push( "client" );

            perkEnergyUiX = builder.comment( "Perk Energy UI display position X" )
                                   .translation( "config.perks.perk_energy_ui_x" )
                                   .defineInRange( "perkEnergyUiX", 5, 0, Integer.MAX_VALUE );

            perkEnergyUiY = builder.comment( "Perk Energy UI display position Y" )
                                   .translation( "config.perks.perk_energy_ui_y" )
                                   .defineInRange( "perkEnergyUiY", 5, 0, Integer.MAX_VALUE );

            builder.pop();
        }
    }
}
