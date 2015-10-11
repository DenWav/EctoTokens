package com.demonwav.ectotoken.button;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.TokensManager;
import com.demonwav.ectotoken.action.Action;
import com.demonwav.ectotoken.gui.Window;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class EctoButton implements Button {

    private final ItemStack stack;
    private final List<Action> actions;
    private final String permission;
    private final long cost;

    @SuppressWarnings("deprecation")
    public EctoButton(String title, List<String> desc, int button, short data, String permission, long cost, List<Action> actions) {
        stack = new ItemStack(button, 1, data);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(title);
            meta.setLore(desc);
            stack.setItemMeta(meta);

        }
        this.actions = actions;
        this.permission = permission;
        this.cost = cost;
    }

    @Override
    public ItemStack getItem() {
        return stack;
    }

    @Override
    public void onClick(Window window, Player player, EctoToken plugin) {
        if (cost == 0) {
            plugin.getServer().getScheduler().runTask(plugin, () ->
                actions.stream().forEach(a -> a.run(window, player, plugin))
            );
        } else {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                if (TokensManager.getInstance().getBalance(player) >= cost) {
                    TokensManager.getInstance().modifyBalance(player, -1 * cost, "SHOP_PURCHASE");
                    plugin.getServer().getScheduler().runTask(plugin, () ->
                        actions.stream().forEach(a -> a.run(window, player, plugin))
                    );
                } else {
                    plugin.getServer().getScheduler().runTask(plugin, () ->
                        player.sendMessage(ChatColor.RED + "You don't have enough tokens to purchase that.")
                    );
                }
            });
        }
    }

    @Override
    public boolean hasPermission(Player player) {
        return permission == null || permission.trim().isEmpty() || player.hasPermission(permission);
    }
}
