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

package net.eidee.minecraft.perks.command.arguments;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.perk.Perk;
import net.eidee.minecraft.perks.registry.PerkRegistry;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkArgument
    implements ArgumentType< Perk >
{
    private static final Collection< String > EXAMPLES = Arrays.asList( "perks:economy", "perks:intelligence" );

    public static final DynamicCommandExceptionType PERK_UNKNOWN = new DynamicCommandExceptionType( ( p_208662_0_ ) -> {
        return new TranslationTextComponent( "commands.perks.unknown", p_208662_0_ );
    } );

    public static PerkArgument perk()
    {
        return new PerkArgument();
    }

    public static Perk getPerk( CommandContext< CommandSource > context, String name )
    {
        return context.getArgument( name, Perk.class );
    }

    @Override
    public Perk parse( StringReader reader )
        throws CommandSyntaxException
    {
        ResourceLocation resourcelocation = ResourceLocation.read( reader );
        return PerkRegistry.getValue( resourcelocation ).orElseThrow( () -> {
            return PERK_UNKNOWN.create( resourcelocation );
        } );
    }

    @Override
    public < S > CompletableFuture< Suggestions > listSuggestions( CommandContext< S > context,
                                                                   SuggestionsBuilder builder )
    {
        return ISuggestionProvider.suggestIterable( PerkRegistry.getKeys(), builder );
    }

    @Override
    public Collection< String > getExamples()
    {
        return EXAMPLES;
    }
}
