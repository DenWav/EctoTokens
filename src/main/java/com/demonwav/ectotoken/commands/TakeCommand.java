package com.demonwav.ectotoken.commands;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.Perm;
import com.demonwav.ectotoken.TokensManager;
import com.demonwav.ectotoken.util.StringUtil;

import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class TakeCommand implements EctoCommand {

    private EctoToken plugin;

    @Override
    public boolean hasPermission(CommandSender sender, String[] args) {
        return sender.hasPermission(Perm.getAdminTake());
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2) {
            sender.sendMessage("Usage: /et take <name> <amount>");
            return true;
        } else {
            final String name = args[0];
            long amount;
            try {
                amount = Long.parseLong(args[1]);
                if (amount < 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException e) {
                sender.sendMessage("<amount> must be an integer value greater than or equal to 0.");
                return true;

            }

            final long finalAmount = amount;
            final String senderName = sender.getName();
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    TokensManager.getInstance().modifyBalance(name, -1 * finalAmount, senderName + " removed tokens");
                    long tokens = TokensManager.getInstance().getBalance(name);
                    if (tokens < 0) {
                        TokensManager.getInstance().setBalance(name, 0, senderName + " removed tokens-overflow");
                        tokens = 0;
                    }

                    final long finalTokens = tokens;
                    plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            sender.sendMessage(StringUtil.formatTokens(finalAmount) + " tokens removed from " + name +
                                "'s account. New balance is " + StringUtil.formatTokens(finalTokens));
                        }
                    });
                }
            });
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
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
