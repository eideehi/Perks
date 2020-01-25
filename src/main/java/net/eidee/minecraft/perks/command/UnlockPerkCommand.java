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
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.command.arguments.PerkArgument;
import net.eidee.minecraft.perks.network.Networks;
import net.eidee.minecraft.perks.network.message.PerkControl;
import net.eidee.minecraft.perks.perk.Perk;
import net.eidee.minecraft.perks.perk.PerkManager;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class UnlockPerkCommand
{
    static void register( CommandDispatcher< CommandSource > dispatcher )
    {
        dispatcher.register( Commands.literal( "unlockPerk" )
                                     .requires( source -> source.hasPermissionLevel( 2 ) )
                                     .then( Commands.argument( "targets", EntityArgument.players() )
                                                    .then( Commands.argument( "perk", PerkArgument.perk() )
                                                                   .executes( UnlockPerkCommand::run ) ) ) );
    }

    private static int run( CommandContext< CommandSource > context )
        throws CommandSyntaxException
    {
        Perk perk = PerkArgument.getPerk( context, "perk" );
        ResourceLocation registryName = perk.getRegistryName();
        if ( registryName != null )
        {
            Collection< ServerPlayerEntity > players = EntityArgument.getPlayers( context, "targets" );
            for ( ServerPlayerEntity player : players )
            {
                if ( PerkManager.of( player ).progressUnlock( perk, 1.0 ) )
                {
                    PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with( () -> player );
                    Networks.CHANNEL.send( target, new PerkControl.UnlockPerk( registryName.toString() ) );

                    context.getSource()
                           .sendFeedback( new TranslationTextComponent( "commands.perks.unlock_perk.query",
                                                                        player.getDisplayName(),
                                                                        I18n.format( perk.getName() ) ),
                                          false );
                }
            }
        }
        return 1;
    }
}
