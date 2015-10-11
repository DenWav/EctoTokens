package com.demonwav.ectotoken.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class EctoInventoryHolder implements InventoryHolder {

    private final EctoWindow window;

    public EctoInventoryHolder(EctoWindow window) {
        this.window = window;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public EctoWindow getWindow() {
        return window;
    }
}
