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

package net.eidee.minecraft.perks.constants;

import static net.eidee.minecraft.perks.PerksMod.MOD_ID;

import net.minecraft.util.ResourceLocation;

public class RegistryKeys
{
    public static final ResourceLocation PERK = new ResourceLocation( MOD_ID, "perk" );

    public static final ResourceLocation PERK_NULL = new ResourceLocation( MOD_ID, "null" );

    public static final ResourceLocation PERK_ENERGY = new ResourceLocation( MOD_ID, "perk_energy" );

    public static final ResourceLocation PERK_EXPERIENCE = new ResourceLocation( MOD_ID, "perk_experience" );

    public static final ResourceLocation PERK_DATA = new ResourceLocation( MOD_ID, "perk_storage" );

    public static final ResourceLocation PERK_EXTRA_DATA = new ResourceLocation( MOD_ID, "perk_extra_data" );

    private RegistryKeys()
    {
    }
}
