package com.demonwav.ectotokens.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class EctoInventoryHolder implements InventoryHolder {

    private final Window window;
    private Inventory inventory;

    public EctoInventoryHolder(Window window) {
        this.window = window;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Window getWindow() {
        return window;
    }
}
