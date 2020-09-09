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

package net.eidee.minecraft.perks.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.constants.RegistryKeys;
import net.eidee.minecraft.perks.perk.Perk;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkRegistry
{
    private static IForgeRegistry< Perk > registry;

    private PerkRegistry()
    {
    }

    public static void init()
    {
        RegistryBuilder< Perk > builder = new RegistryBuilder<>();
        builder.setName( RegistryKeys.PERK );
        builder.setType( Perk.class );
        registry = builder.create();
    }

    private static IForgeRegistry< Perk > get()
    {
        return registry;
    }

    public static Collection< ResourceLocation > getKeys()
    {
        return Collections.unmodifiableSet( get().getKeys() );
    }

    public static ResourceLocation getKey( Perk value )
    {
        ResourceLocation key = get().getKey( value );
        return key != null ? key : RegistryKeys.PERK_NULL;
    }

    public static Optional< Perk > getValue( ResourceLocation key )
    {
        return Optional.ofNullable( get().getValue( key ) );
    }

    public static Optional< Perk > getValue( String name )
    {
        return getValue( new ResourceLocation( name ) );
    }
}
