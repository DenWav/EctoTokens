package com.demonwav.ectotoken.gui;

import com.demonwav.ectotoken.EctoToken;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Defines some Page, which any {@link Window} will have a list of. If you define your own window by implementing
 * {@link Window}, you can either implement this class yourself, or just use the {@link EctoPage} implementation.
 */
public interface Page {

    /**
     * Return the number of buttons which populate this Page.
     *
     * @return
     */
    int getNumItems();

    /**
     * Using the provided inventory, "draw" this page.
     *
     * @param inv The inventory to display this page on
     */
    void applyPage(Inventory inv);

    /**
     * Called when an button is clicked. This is passed to the page so the page can use whatever logic necessary to
     * process the click. This can include passing it to the Button's onClick(), or handling it in the Page itself.
     *
     * @param slot The numerical slot of the inventory where the player clicked.
     * @param player The Player who clicked the Button.
     * @param window The Window which owns this Page.
     * @param plugin The instance of the EctoToken plugin.
     */
    void click(int slot, Player player, Window window, EctoToken plugin);
}
