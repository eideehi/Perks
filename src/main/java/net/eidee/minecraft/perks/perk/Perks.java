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

import net.eidee.minecraft.perks.constants.RegistryNames;

import net.minecraftforge.registries.ObjectHolder;

public class Perks
{
    @ObjectHolder( RegistryNames.PERK_ECONOMY )
    public static Perk ECONOMY;

    @ObjectHolder( RegistryNames.PERK_INTELLIGENCE )
    public static Perk INTELLIGENCE;

    @ObjectHolder( RegistryNames.PERK_IRON_FIST )
    public static Perk IRON_FIST;

    @ObjectHolder( RegistryNames.PERK_CULTIVATOR )
    public static Perk CULTIVATOR;

    @ObjectHolder( RegistryNames.PERK_SEEDER )
    public static Perk SEEDER;

    @ObjectHolder( RegistryNames.PERK_SPREADER )
    public static Perk SPREADER;

    @ObjectHolder( RegistryNames.PERK_HARVESTER )
    public static Perk HARVESTER;

    @ObjectHolder( RegistryNames.PERK_FURNACEMAN )
    public static Perk FURNACEMAN;

    @ObjectHolder( RegistryNames.PERK_RESIDENT_OF_END )
    public static Perk RESIDENT_OF_END;

    @ObjectHolder( RegistryNames.PERK_BREEDER )
    public static Perk BREEDER;

    @ObjectHolder( RegistryNames.PERK_FEEDER )
    public static Perk FEEDER;

    @ObjectHolder( RegistryNames.PERK_OBSIDIAN_BREAKER )
    public static Perk OBSIDIAN_BREAKER;

    @ObjectHolder( RegistryNames.PERK_POLISHER )
    public static Perk POLISHER;
}
