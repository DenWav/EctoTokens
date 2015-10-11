package com.demonwav.ectotoken.events;

import com.demonwav.ectotoken.gui.EctoInventoryHolder;
import com.demonwav.ectotoken.gui.EctoWindow;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class EctoInventoryClickEvent implements Listener {

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

        // But we only want to process left clicks
        if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.SHIFT_LEFT) {
            EctoWindow window = ((EctoInventoryHolder) event.getInventory().getHolder()).getWindow();
            window.click(event.getRawSlot(), event.getClick());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCloseEvent(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof EctoInventoryHolder)
            ((EctoInventoryHolder) event.getInventory().getHolder()).getWindow().close();
    }

}
