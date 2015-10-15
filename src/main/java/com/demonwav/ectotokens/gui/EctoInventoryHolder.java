package com.demonwav.ectotokens.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class EctoInventoryHolder implements InventoryHolder {

    private final Window window;

    public EctoInventoryHolder(Window window) {
        this.window = window;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public Window getWindow() {
        return window;
    }
}
