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

package net.eidee.minecraft.perks.command;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.network.Networks;
import net.eidee.minecraft.perks.network.message.Sync;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkExperienceCommand
{
    static void register( CommandDispatcher< CommandSource > dispatcher )
    {
        dispatcher.register( Commands.literal( "perkExperience" )
                                     .requires( source -> source.hasPermissionLevel( 2 ) )
                                     .then( Commands.literal( "add" )
                                                    .then( Commands.argument( "targets", EntityArgument.players() )
                                                                   .then( Commands.argument( "amount", IntegerArgumentType.integer( 0 ) )
                                                                                  .executes( PerkExperienceCommand::add ) ) ) )
                                     .then( Commands.literal( "set" )
                                                    .then( Commands.argument( "targets", EntityArgument.players() )
                                                                   .then( Commands.argument( "value", IntegerArgumentType.integer( 0 ) )
                                                                                  .executes( PerkExperienceCommand::set ) ) ) ) );
    }

    private static int add( CommandContext< CommandSource > context )
        throws CommandSyntaxException
    {
        CommandSource source = context.getSource();
        Collection< ServerPlayerEntity > players = EntityArgument.getPlayers( context, "targets" );
        int amount = IntegerArgumentType.getInteger( context, "amount" );
        for ( ServerPlayerEntity player : players )
        {
            PerkManager manager = PerkManager.of( player );
            manager.addPerkExperience( amount );

            PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with( () -> player );
            Networks.CHANNEL.send( target, new Sync.PerkExperience( manager.getPerkExperience().serializeNBT() ) );

            source.sendFeedback( new TranslationTextComponent( "commands.perks.perk_experience.query.add",
                                                               player.getDisplayName(),
                                                               amount ),
                                 false );
        }

        return 1;
    }

    private static int set( CommandContext< CommandSource > context )
        throws CommandSyntaxException
    {
        CommandSource source = context.getSource();
        Collection< ServerPlayerEntity > players = EntityArgument.getPlayers( context, "targets" );
        int value = IntegerArgumentType.getInteger( context, "value" );
        for ( ServerPlayerEntity player : players )
        {
            PerkManager manager = PerkManager.of( player );
            int currentExperience = manager.getPerkExperience().getValue();
            manager.addPerkExperience( value - currentExperience );

            PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with( () -> player );
            Networks.CHANNEL.send( target, new Sync.PerkExperience( manager.getPerkExperience().serializeNBT() ) );

            source.sendFeedback( new TranslationTextComponent( "commands.perks.perk_experience.query.set",
                                                               player.getDisplayName(),
                                                               value ),
                                 false );
        }
        return 1;
    }
}
