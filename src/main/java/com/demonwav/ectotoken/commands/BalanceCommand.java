package com.demonwav.ectotoken.commands;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.Perm;
import com.demonwav.ectotoken.TokensManager;
import com.demonwav.ectotoken.util.StringUtil;

import lombok.AllArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 0 && sender instanceof OfflinePlayer) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    final long amount = TokensManager.getInstance().getBalance(((OfflinePlayer) sender));
                    final String name = sender.getName();
                    final String text = plugin.getMainConfig().getCommands().getBalance();
                    plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            sender.sendMessage(StringUtil.balanceTextVar(text, name, amount));
                        }
                    });
                }
            });
        } else if (args.length > 0) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    final String name = args[0];
                    final long amount = TokensManager.getInstance().getBalance(name);
                    final String text = plugin.getMainConfig().getCommands().getBalance();
                    plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            sender.sendMessage(StringUtil.balanceTextVar(text, name, amount));
                        }
                    });
                }
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
            List<String> players = new ArrayList<>();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    players.add(player.getName());
                }
            }
            players.sort(String.CASE_INSENSITIVE_ORDER);
            return players;
        }
    }
}
