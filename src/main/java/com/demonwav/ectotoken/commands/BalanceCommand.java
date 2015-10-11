package com.demonwav.ectotoken.commands;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.Perm;
import com.demonwav.ectotoken.TokensManager;
import com.demonwav.ectotoken.util.StringUtil;

import lombok.AllArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class BalanceCommand implements EctoCommand {

    private EctoToken plugin;

    @Override
    public boolean hasPermission(CommandSender sender, String[] args) {
        if (args == null || args.length == 0)
            return sender.hasPermission(Perm.getPlayerBalance());
        else
            return sender.hasPermission(Perm.getPlayerBalanceOthers());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 && sender instanceof OfflinePlayer) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                long amount = TokensManager.getInstance().getBalance(((OfflinePlayer) sender));
                String name = sender.getName();
                String text = plugin.getMainConfig().getCommands().getBalance();
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        sender.sendMessage(StringUtil.balanceTextVar(text, name, amount))
                );
            });
        } else if (args.length > 0) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                String name = args[0];
                long amount = TokensManager.getInstance().getBalance(name);
                String text = plugin.getMainConfig().getCommands().getBalance();
                plugin.getServer().getScheduler().runTask(plugin, () ->
                    sender.sendMessage(StringUtil.balanceTextVar(text, name, amount))
                );
            });
        } else {
            sender.sendMessage("You must be a player to run this command with no arguments.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        System.out.println(Arrays.toString(args));
        if (args.length != 1) {
            return Collections.emptyList();
        } else {
            List<String> players = plugin.getServer().getOnlinePlayers().stream().filter(
                player -> player.getName().toLowerCase().toLowerCase().startsWith(args[0].toLowerCase())
            ).collect(
                Collectors.mapping(OfflinePlayer::getName, Collectors.toList())
            );
            players.sort(String.CASE_INSENSITIVE_ORDER);
            return players;
        }
    }
}
