package net.eidee.minecraft.perks.init;

import com.mojang.brigadier.CommandDispatcher;
import net.eidee.minecraft.perks.command.PerkCommand;

import net.minecraft.command.CommandSource;

public class CommandInitializer
{
    public static void registerCommand( CommandDispatcher< CommandSource > dispatcher )
    {
        PerkCommand.register( dispatcher );
    }
}
