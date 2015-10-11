package com.demonwav.ectotoken.commands;

import com.demonwav.ectotoken.CouponManager;
import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.Perm;
import com.demonwav.ectotoken.util.StringUtil;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class GenerateCommand implements EctoCommand {

    private EctoToken plugin;

    @Override
    public boolean hasPermission(CommandSender sender, String[] args) {
        return sender.hasPermission(Perm.getAdminGenerate());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 2) {
            String name;
            long amount;
            int uses = 0;
            if (args[0].startsWith("\"")) {
                // Collapse quoted arguments down to one argument
                StringBuilder sb = new StringBuilder();
                boolean loop = true;
                int i = 0;
                while (loop) {
                    if (args.length == i) {
                        sender.sendMessage("Usage: /et generate <name> <amount> [usages]");
                        return true;
                    }
                    String val = args[i];
                    if (args[i].endsWith("\"")) {
                        loop = false;
                        val = args[i].substring(0, args[i].length() - 1);
                    }
                    if (args[i].startsWith("\"")) {
                        val = args[i].substring(1);
                    }
                    sb.append(val);
                    if (loop) {
                        sb.append(" ");
                        i++;
                    }
                }
                // Get coupon amount
                i++;
                if (args.length == i) {
                    sender.sendMessage("Usage: /et generate <name> <amount> [usages]");
                    return true;
                }
                name = sb.toString();
                try {
                    amount = Long.parseLong(args[i]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("<amount> must be an integer value greater than or equal to zero.");
                    return true;
                }
                // Get uses amount, if present
                i++;
                if (args.length > i) {
                    try {
                        uses = Integer.parseInt(args[i]);
                        if (uses < 0)
                            throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        sender.sendMessage("[uses] must be an integer value greater than or equal to zero.");
                        return true;
                    }
                }
            } else {
                name = args[0];
                try {
                    amount = Long.parseLong(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("Usage: /et generate <name> <amount> [usages]");
                    return true;
                }
                if (args.length == 3) {
                    try {
                        uses = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("[uses] must be an integer value greater than or equal to zero.");
                        return true;
                    }
                }
            }

            if (name.length() > 50) {
                sender.sendMessage(ChatColor.RED + "Coupon name too long.");
            } else {
                boolean worked = CouponManager.getInstance().createCoupon(name, amount, uses);
                if (worked) {
                    String usesString = uses == 0 ? "unlimited uses" : uses + " uses";
                    sender.sendMessage(ChatColor.AQUA + "Coupon with name " + ChatColor.BLUE + name + ChatColor.AQUA +
                        " for amount " + ChatColor.BLUE + StringUtil.formatTokens(amount) + ChatColor.AQUA +
                        " for " + ChatColor.BLUE + usesString + ChatColor.AQUA + " created successfully.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Coupon failed to create.");
                }
            }
        } else {
            sender.sendMessage("Usage: /et generate <name> <amount> [usages]");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
