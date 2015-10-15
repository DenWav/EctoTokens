package com.demonwav.ectotokens.commands;

import com.demonwav.ectotokens.EctoTokens;
import com.demonwav.ectotokens.Perm;

import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class AdminCommand implements EctoCommand {

    private EctoTokens plugin;

    @Override
    public boolean hasPermission(CommandSender sender, String[] args) {
        return sender.hasPermission(Perm.getAdminHelp());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(plugin.getMainConfig().getCommands().getHelp().getAdminHelp());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if ("help".startsWith(args[0].toLowerCase())) {
            return Collections.singletonList("help");
        } else {
            return Collections.emptyList();
        }
    }
}
