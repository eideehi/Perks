package net.eidee.minecraft.perks.command;

import java.util.Collection;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.capability.Capabilities;
import net.eidee.minecraft.perks.command.arguments.PerkArgument;
import net.eidee.minecraft.perks.perk.Perk;
import net.eidee.minecraft.perks.perk.PerkManager;
import net.eidee.minecraft.perks.util.Message;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerkCommand
{
    private static int removePerk( CommandContext< CommandSource > context )
        throws CommandSyntaxException
    {
        Perk perk = PerkArgument.getPerk( context, "perk" );
        ResourceLocation registryName = perk.getRegistryName();
        if ( registryName != null )
        {
            Collection< ServerPlayerEntity > players = EntityArgument.getPlayers( context, "targets" );
            for ( ServerPlayerEntity player : players )
            {
                if ( PerkManager.createController( player, perk ).removePerk() )
                {
                    context.getSource()
                           .sendFeedback( Message.of( "commands.perks.remove_perk.query",
                                                      perk.getDisplayName(),
                                                      player.getDisplayName() ), false );
                }
            }
        }
        return 1;
    }

    private static int unlockPerk( CommandContext< CommandSource > context )
        throws CommandSyntaxException
    {
        Perk perk = PerkArgument.getPerk( context, "perk" );
        ResourceLocation registryName = perk.getRegistryName();
        if ( registryName != null )
        {
            Collection< ServerPlayerEntity > players = EntityArgument.getPlayers( context, "targets" );
            for ( ServerPlayerEntity player : players )
            {
                if ( PerkManager.createController( player, perk ).unlockPerk( true ) )
                {
                    context.getSource()
                           .sendFeedback( Message.of( "commands.perks.unlock_perk.query",
                                                      player.getDisplayName(),
                                                      perk.getDisplayName() ), false );
                }
            }
        }
        return 1;
    }

    private static int addExperience( CommandContext< CommandSource > context )
        throws CommandSyntaxException
    {
        CommandSource source = context.getSource();
        Collection< ServerPlayerEntity > players = EntityArgument.getPlayers( context, "targets" );
        int amount = IntegerArgumentType.getInteger( context, "amount" );
        for ( ServerPlayerEntity player : players )
        {
            player.getCapability( Capabilities.PERK_EXPERIENCE ).ifPresent( experience -> {
                experience.receiveExperience( amount, false );

                source.sendFeedback( Message.of( "commands.perks.add_experience.query",
                                                 player.getDisplayName().getString(),
                                                 amount ), false );
            } );
        }

        return 1;
    }

    private static int setExperience( CommandContext< CommandSource > context )
        throws CommandSyntaxException
    {
        CommandSource source = context.getSource();
        Collection< ServerPlayerEntity > players = EntityArgument.getPlayers( context, "targets" );
        int value = IntegerArgumentType.getInteger( context, "value" );
        for ( ServerPlayerEntity player : players )
        {
            player.getCapability( Capabilities.PERK_EXPERIENCE ).ifPresent( experience -> {
                int exp = experience.getExperience();
                if ( value > exp )
                {
                    experience.receiveExperience( value - exp, false );
                }
                else
                {
                    experience.extractExperience( exp - value, false );
                }

                source.sendFeedback( Message.of( "commands.perks.set_experience.query",
                                                 player.getDisplayName(),
                                                 value ), false );
            } );
        }
        return 1;
    }

    public static void register( CommandDispatcher< CommandSource > dispatcher )
    {
        dispatcher.register( Commands.literal( "perk" )
                                     .requires( source -> source.hasPermissionLevel( 2 ) )
                                     .then( Commands.literal( "experience" )
                                                    .then( Commands.literal( "add" )
                                                                   .then( Commands.argument( "targets", EntityArgument.players() )
                                                                                  .then( Commands.argument( "amount", IntegerArgumentType.integer( 0 ) )
                                                                                                 .executes( PerkCommand::addExperience ) ) ) )
                                                    .then( Commands.literal( "set" )
                                                                   .then( Commands.argument( "targets", EntityArgument.players() )
                                                                                  .then( Commands.argument( "value", IntegerArgumentType.integer( 0 ) )
                                                                                                 .executes( PerkCommand::setExperience ) ) ) ) )
                                     .then( Commands.literal( "unlock" )
                                                    .then( Commands.argument( "targets", EntityArgument.players() )
                                                                   .then( Commands.argument( "perk", PerkArgument.perk() )
                                                                                  .executes( PerkCommand::unlockPerk ) ) ) )
                                     .then( Commands.literal( "remove" )
                                                    .then( Commands.argument( "targets", EntityArgument.players() )
                                                                   .then( Commands.argument( "perk", PerkArgument.perk() )
                                                                                  .executes( PerkCommand::removePerk ) ) ) ) );
    }
}
