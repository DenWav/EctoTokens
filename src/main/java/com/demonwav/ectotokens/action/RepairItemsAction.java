package com.demonwav.ectotokens.action;

import com.demonwav.ectotokens.EctoTokens;
import com.demonwav.ectotokens.TokensManager;
import com.demonwav.ectotokens.config.shop.actions.RepairItemsActionConfig;
import com.demonwav.ectotokens.gui.Window;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RepairItemsAction extends Action {

    private final RepairItemsActionConfig config;

    public RepairItemsAction(RepairItemsActionConfig config) {
        this.config = config;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void run(Window window, Player player, EctoTokens plugin) {
        int start = 0;
        if (config.getOnlyEquippedItems())
            start = 36;
        for (int i = start; i < 40; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack != null) {
                if (config.getItemTypes().isEmpty() ||  config.getItemTypes().contains(stack.getTypeId())) {
                    double damage = stack.getDurability();
                    Material material = stack.getType();
                    double percentage = damage / (double)material.getMaxDurability();
                    long cost = Math.round(percentage * config.getCost());
                    TokensManager.getInstance().modifyBalance(player, -1 * cost, "REPAIR_COST");
                    stack.setDurability((short) 0);
                }
            }
        }
        window.updateActionBar();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean checkAction(Window window, Player player, EctoTokens plugin) {
        int start = 0;
        if (config.getOnlyEquippedItems())
            start = 36;
        long runningCost = 0;
        for (int i = start; i < 40; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack != null) {
                if (config.getItemTypes().isEmpty() || config.getItemTypes().contains(stack.getTypeId())) {
                    double damage = stack.getDurability();
                    Material material = stack.getType();
                    double percentage = damage / (double) material.getMaxDurability();
                    runningCost += Math.round(percentage * config.getCost());
                }
            }
        }
        return TokensManager.getInstance().getBalance(player) >= runningCost;
    }
}
