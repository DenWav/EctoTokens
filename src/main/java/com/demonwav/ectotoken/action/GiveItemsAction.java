package com.demonwav.ectotoken.action;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.config.shop.actions.GiveItemsActionConfig;
import com.demonwav.ectotoken.gui.Window;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.stream.IntStream;

public class GiveItemsAction implements Action {

    private final GiveItemsActionConfig config;

    public GiveItemsAction(GiveItemsActionConfig config) {
        this.config = config;
    }

    @Override
    public void run(Window window, Player player, EctoToken plugin) {
        config.getItems().stream().forEach(item -> {
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
        });
        window.updateActionBar();
    }

    @Override
    public boolean checkAction(Window window, Player player, EctoToken plugin) {
        int stacks = 0;
        for (GiveItemsActionConfig.Item item : config.getItems()) {
            ItemStack stack = new ItemStack(item.getItemId(), 1, item.getItemData());
            int stackSize = stack.getMaxStackSize();
            stacks += Math.ceil((float)item.getQuantity() / (float)stackSize);
        }
        long free = IntStream.range(0, 36).filter(i -> player.getInventory().getItem(i) == null).count();
        return free >= stacks;
    }
}
