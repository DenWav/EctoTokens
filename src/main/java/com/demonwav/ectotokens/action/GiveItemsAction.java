package com.demonwav.ectotokens.action;

import com.demonwav.ectotokens.EctoTokens;
import com.demonwav.ectotokens.config.shop.actions.GiveItemsActionConfig;
import com.demonwav.ectotokens.gui.Window;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GiveItemsAction extends Action {

    private final GiveItemsActionConfig config;

    public GiveItemsAction(GiveItemsActionConfig config) {
        this.config = config;
    }

    @Override
    public void run(Window window, Player player, EctoTokens plugin) {
        for (GiveItemsActionConfig.Item item : config.getItems()) {
            ItemStack stack = new ItemStack(item.getItemId(), 1, item.getItemData());
            int maxStack = stack.getMaxStackSize();
            int quantity = item.getQuantity();
            do {
                int tempQuantity = quantity;
                if (quantity > maxStack)
                    tempQuantity = maxStack;
                ItemStack giveStack = new ItemStack(item.getItemId(), tempQuantity, item.getItemData());
                // Set other properties
                ItemMeta meta = giveStack.getItemMeta();
                if (!item.getDisplayName().isEmpty())
                    meta.setDisplayName(item.getDisplayName());
                meta.setLore(item.getLore());
                giveStack.setItemMeta(meta);
                // Set enchant
                for (GiveItemsActionConfig.Item.Enchant enchant : item.getEnchants()) {
                    Enchantment enchantment = Enchantment.getByName(enchant.getEnchantment());
                    giveStack.addEnchantment(enchantment, enchant.getLevel());
                }
                player.getInventory().addItem(giveStack);
                quantity -= tempQuantity;
            } while (quantity > 0);
        }
        window.updateActionBar();
    }

    @Override
    public boolean checkAction(Window window, Player player, EctoTokens plugin) {
        int stacks = 0;
        for (GiveItemsActionConfig.Item item : config.getItems()) {
            ItemStack stack = new ItemStack(item.getItemId(), 1, item.getItemData());
            int stackSize = stack.getMaxStackSize();
            stacks += Math.ceil((float)item.getQuantity() / (float)stackSize);
        }
        long free = 0;
        for (int i = 0; i < 36; i++) {
            if (player.getInventory().getItem(i) == null)
                free++;
        }
        return free >= stacks;
    }
}
