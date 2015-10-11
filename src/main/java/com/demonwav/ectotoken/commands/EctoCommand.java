package com.demonwav.ectotoken.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public interface EctoCommand extends CommandExecutor, TabCompleter {

    boolean hasPermission(CommandSender sender, String[] args);
}
