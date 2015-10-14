package com.demonwav.ectotoken.button;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.TokensManager;
import com.demonwav.ectotoken.action.Action;
import com.demonwav.ectotoken.gui.Window;
import com.demonwav.ectotoken.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class EctoButton extends Button {

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
    public void onClick(final Window window, final Player player, final EctoToken plugin) {
        if (cost == 0) {
            Util.runTask(new Runnable() {
                @Override
                public void run() {
                    for (Action action : actions) {
                        action.run(window, player, plugin);
                    }
                }
            });
        } else {
            Util.runTaskAsync(new Runnable() {
                @Override
                public void run() {
                    if (TokensManager.getInstance().getBalance(player) >= cost) {
                        TokensManager.getInstance().modifyBalance(player, -1 * cost, "SHOP_PURCHASE");
                        Util.runTask(new Runnable() {
                            @Override
                            public void run() {
                                for (Action action : actions) {
                                    action.run(window, player, plugin);
                                }
                            }
                        });
                    } else {
                        Util.runTask(new Runnable() {
                            @Override
                            public void run() {
                                player.sendMessage(ChatColor.RED + "You don't have enough tokens to purchase that.");
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public boolean hasPermission(Player player) {
        return permission == null || permission.trim().isEmpty() || player.hasPermission(permission);
    }
}
