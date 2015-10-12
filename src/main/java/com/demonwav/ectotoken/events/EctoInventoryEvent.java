package com.demonwav.ectotoken.events;

import com.demonwav.ectotoken.gui.EctoInventoryHolder;
import com.demonwav.ectotoken.gui.Window;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class EctoInventoryEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClickEvent(InventoryClickEvent event) {
        // We don't want anything to happen in this type of inventory ever
        if (event.getInventory().getHolder() instanceof EctoInventoryHolder)
            event.setCancelled(true);
        else
            return;

        // Clicking outside of the inventory
        if (event.getRawSlot() < 0)
            return;

        Window window = ((EctoInventoryHolder) event.getInventory().getHolder()).getWindow();
        window.click(event.getRawSlot(), event.getClick());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCloseEvent(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof EctoInventoryHolder)
            ((EctoInventoryHolder) event.getInventory().getHolder()).getWindow().close();
    }
}
