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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.registry.PerkRegistry;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkStorage
    implements INBTSerializable< ListNBT >
{
    private Map< Perk, PerkData > storage;

    public PerkStorage()
    {
        this.storage = new HashMap<>();
    }

    Map< Perk, PerkData > getRawStorage()
    {
        return storage;
    }

    public Map< Perk, PerkData > getStorage()
    {
        return Collections.unmodifiableMap( storage.entrySet()
                                                   .stream()
                                                   .filter( entry -> entry.getKey().isAvailable() )
                                                   .collect( Collectors.toMap( Map.Entry::getKey,
                                                                               Map.Entry::getValue ) ) );
    }

    public PerkData getData( Perk perk )
    {
        if ( perk.isAvailable() )
        {
            return storage.computeIfAbsent( perk, key -> PerkData.createDefault() );
        }
        return PerkData.createDefault();
    }

    @Override
    public ListNBT serializeNBT()
    {
        ListNBT nbt = new ListNBT();
        storage.forEach( ( perk, perkData ) -> {
            ResourceLocation registryName = perk.getRegistryName();
            if ( registryName != null )
            {
                CompoundNBT compound = new CompoundNBT();
                compound.putString( "Perk", registryName.toString() );
                compound.put( "PerkData", PerkData.toNBT( perkData ) );
                nbt.add( compound );
            }
        } );
        return nbt;
    }

    @Override
    public void deserializeNBT( ListNBT nbt )
    {
        storage = new HashMap<>();
        for ( int i = 0; i < nbt.size(); i++ )
        {
            CompoundNBT compound = nbt.getCompound( i );
            PerkRegistry.getValue( compound.getString( "Perk" ) ).ifPresent( perk -> {
                storage.put( perk, PerkData.fromNBT( compound.getCompound( "PerkData" ) ) );
            } );
        }
    }
}
