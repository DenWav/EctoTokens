package com.demonwav.ectotoken.commands;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.Perm;
import com.demonwav.ectotoken.TokensManager;
import com.demonwav.ectotoken.util.StringUtil;

import lombok.AllArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class SetCommand implements EctoCommand {

    private EctoToken plugin;

    @Override
    public boolean hasPermission(CommandSender sender, String[] args) {
        return sender.hasPermission(Perm.getAdminSet());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("Usage: /et set <name> <amount>");
            return true;
        } else {
            String name = args[0];
            long amount = 0;
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
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    TokensManager.getInstance().setBalance(name, finalAmount, senderName + " set tokens");
                    plugin.getServer().getScheduler().runTask(plugin, () ->
                    sender.sendMessage(name + "'s balance set to " + StringUtil.formatTokens(finalAmount) + " tokens."));
                }
            );
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        } else {
            List<String> players = plugin.getServer().getOnlinePlayers().stream().filter(
                player -> player.getName().toLowerCase().startsWith(args[0].toLowerCase())
            ).collect(
                Collectors.mapping(OfflinePlayer::getName, Collectors.toList())
            );
            players.sort(String.CASE_INSENSITIVE_ORDER);
            return players;
        }
    }
}
