package com.demonwav.ectotoken.gui;

import com.demonwav.ectotoken.EctoToken;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface Page {
    int getNumItems();
    void applyPage(Inventory inv);
    void click(int slot, Player player, Window window, EctoToken plugin);
}
