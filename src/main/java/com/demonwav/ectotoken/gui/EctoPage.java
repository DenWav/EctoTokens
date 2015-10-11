package com.demonwav.ectotoken.gui;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.button.Button;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EctoPage implements Page {

    private final int h;
    // These two objects who the same data, so little memory increase
    private final List<Button> buttons;
    private final Map<Integer, Button> slotsMap = new HashMap<>();

    public EctoPage(int h, List<Button> buttons) {
        this.h = h;
        this.buttons = buttons;
    }

    public int getNumItems() {
        return buttons.size();
    }

    public void applyPage(Inventory inv) {
        Iterator<Button> iterator = buttons.iterator();
        // start at 10 because this is the first available slot in the inventory. This is because 0 - 8 is the first
        // row, and 9 is the left reserve column
        // 9 * h gives the total slots, - 1 because it's 0 indexed
        for (int i = 10; i < (9*h-1); i++) {
            // reserved columns
            if (i % 9 == 0 || (i+1) % 9 == 0)
                continue;

            // only keep going while there's buttons to show
            if (!iterator.hasNext())
                break;

            Button button = iterator.next();
            inv.setItem(i, button.getItem());
            slotsMap.put(i, button);
        }
    }

    public void click(int slot, Player player, Window window, EctoToken plugin) {
        if (slotsMap.containsKey(slot)) {
            slotsMap.get(slot).onClick(window, player, plugin);
        }
    }
}
