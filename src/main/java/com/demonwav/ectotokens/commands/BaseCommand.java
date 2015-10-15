package com.demonwav.ectotokens.commands;

import com.demonwav.ectotokens.EctoTokens;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseCommand implements CommandExecutor, TabCompleter {

    private EctoTokens plugin;
    private HashMap<String, EctoCommand> subCommands = new HashMap<>();

    public BaseCommand(EctoTokens plugin) {
        this.plugin = plugin;
    }

    public void registerCommand(String cmd, EctoCommand command) {
        if (subCommands.containsKey(cmd))
            throw new CommandAlreadyDefinedException(cmd);
        subCommands.put(cmd, command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Special case, show help on 0 args
            EctoCommand help = subCommands.get("help");
            if (help.hasPermission(sender, null)) {
                return help.onCommand(sender, command, label, args);
            } else {
                sender.sendMessage("You don't have permission to run that command.");
            }
        } else {
            if (subCommands.containsKey(args[0])) {
                EctoCommand ectoCommand = subCommands.get(args[0]);
                String[] subArgs = new String[args.length - 1];
                System.arraycopy(args, 1, subArgs, 0, subArgs.length);
                if (ectoCommand.hasPermission(sender, subArgs)) {
                    return ectoCommand.onCommand(sender, command, label, subArgs);
                } else {
                    sender.sendMessage("You don't have permission to run that command.");
                }
            } else {
                sender.sendMessage("Command not found.");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> result = new ArrayList<>();
            for (Map.Entry<String, EctoCommand> entry : subCommands.entrySet()) {
                if (entry.getValue().hasPermission(sender, null) &&
                        entry.getKey().toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(entry.getKey());
                }
            }
            result.sort(String.CASE_INSENSITIVE_ORDER);
            return result;
        } else {
            if (subCommands.containsKey(args[0])) {
                EctoCommand ectoCommand = subCommands.get(args[0]);
                String[] subArgs = new String[args.length - 1];
                System.arraycopy(args, 1, subArgs, 0, subArgs.length);
                if (ectoCommand.hasPermission(sender, subArgs)) {
                    return ectoCommand.onTabComplete(sender, command, alias, subArgs);
                } else {
                    return Collections.emptyList();
                }
            } else {
                return Collections.emptyList();
            }
        }
    }
}
