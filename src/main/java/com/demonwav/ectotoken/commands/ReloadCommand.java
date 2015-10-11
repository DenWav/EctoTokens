package com.demonwav.ectotoken.commands;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.Perm;

import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class ReloadCommand implements EctoCommand {

    private EctoToken plugin;

    @Override
    public boolean hasPermission(CommandSender sender, String[] args) {
        if (args == null || args.length == 0) {
            return sender.hasPermission(Perm.getAdminReload());
        } else {
            switch (args[0]) {
                case"config":
                    return sender.hasPermission(Perm.getAdminReloadConfig());
                case "shop":
                    return sender.hasPermission(Perm.getAdminReloadShop());
                case "tokens":
                    return sender.hasPermission(Perm.getAdminReloadTokens());
                default:
                    return true;
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            plugin.loadConfigs();
            if (plugin.isEnabled()) {
                sender.sendMessage("All config files reloaded successfully.");
            } else {
                sender.sendMessage("There was an error while trying to reload the configs. Disabled.");
            }
        } else {
            String name;
            switch (args[0]) {
                case "config":
                    name = "Main";
                    plugin.loadMainConfig();
                    break;
                case "shop":
                    name = "Shop";
                    plugin.loadShopConfig();
                    break;
                case "tokens":
                    name = "Tokens";
                    plugin.loadTokensConfig();
                    break;
                default:
                    sender.sendMessage("Command not found.");
                    return true;
            }
            if (plugin.isEnabled())
                sender.sendMessage(name + " config reloaded successfully.");
            else
                sender.sendMessage("There was an error while trying to reload the config. Disabled.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if ("config".startsWith(args[0].toLowerCase()))
            return Collections.singletonList("config");
        if ("shop".startsWith(args[0].toLowerCase()))
            return Collections.singletonList("shop");
        if ("tokens".startsWith(args[0].toLowerCase()))
            return Collections.singletonList("tokens");
        return Collections.emptyList();
    }
}
