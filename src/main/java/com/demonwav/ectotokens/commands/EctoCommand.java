package com.demonwav.ectotokens.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

/**
 * This interface defines a subcommand for {@link BaseCommand}. It defines it's own hasPermission method to check if
 * a CommandSender has permission to use the command. This allows the BaseCommand to handle permission checks, meaning
 * there is no need to check permission in onCommand().
 */
public interface EctoCommand extends CommandExecutor, TabCompleter {

    /**
     * Check whether a CommandSender has permission to use this command with the given argument list.
     *
     * @param sender The CommandSender to check permission for
     * @param args The argument list to check permission for
     * @return True if the CommandSender is allowed to run this command with these arguments
     */
    boolean hasPermission(CommandSender sender, String[] args);
}
