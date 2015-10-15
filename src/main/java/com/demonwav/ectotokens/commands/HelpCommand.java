package com.demonwav.ectotokens.commands;

import com.demonwav.ectotokens.EctoTokens;
import com.demonwav.ectotokens.Perm;

import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class HelpCommand implements EctoCommand {

    private EctoTokens plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(plugin.getMainConfig().getCommands().getHelp().getPlayerHelp());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean hasPermission(CommandSender sender, String[] args) {
        return sender.hasPermission(Perm.getPlayerHelp());
    }
}
