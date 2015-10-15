package com.demonwav.ectotokens.gui;

import com.demonwav.ectotokens.action.OpenWindowAction;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

// TODO: update this when extending documentation is available
/**
 * This defines a window which can be opened either as the root window, or a window opened by an
 * {@link OpenWindowAction}. More information on how to do this will be added later.
 * <p>
 * The EctoInventoryHolder is a utility class to help Window operations. If the Inventory you create in
 * {@link #createWindow()} has an InventoryHolder which is an instanceof {@link EctoInventoryHolder} then the
 * {@link #click(int, ClickType)} method will be called automatically for you and you won't have to create your own
 * InventoryClickEvent listener. Similarly, if you do this the {@link #close()} method will also be called for you, and
 * you won't have to create your own InventoryCloseEvent listener. The EctoInventoryHolder is simply a way to identify
 * which inventories are managed by a Window, and an easy way to get the Window back from the Inventory. You don't have
 * to use it, you can create your own listeners to handle the events yourself if you wish.
 */
public interface Window {

    /**
     * Get the player this Window is being shown to.
     *
     * @return The player this Window is being shown to.
     */
    Player getPlayer();

    /**
     * Create the Inventory and open it for the Player.
     */
    void createWindow();

    /**
     * Get the title of this Window.
     *
     * @return The title of this Window.
     */
    String getTitle();

    /**
     * Get how many Buttons are on this Window.
     *
     * @return How many Buttons are on this Window.
     */
    int getNumItems();

    /**
     * Get the height of this Window. That is, the number of rows.
     *
     * @return The height of this Window.
     */
    int getHeight();

    /**
     * Get whether or not his Window has a parent Window.
     *
     * @return True if this Window has a parent.
     */
    boolean hasParent();

    /**
     * The parent Window for this Window. Null if this Window does not have a parent.
     *
     * @return The parent Window for this Window, if it exists.
     */
    Window getParent();

    /**
     * Pass a click event for the provided slot.
     *
     * @param slot The slot where the Player clicked.
     * @param type The type of click.
     */
    void click(int slot, ClickType type);

    /**
     * Run whatever logic necessary to clean up after this Window has been closed.
     */
    void close();

    /**
     * Check if there is a Page after the current Page.
     *
     * @return True if there is a Page after the current Page.
     */
    boolean hasNextPage();

    /**
     * Check if there is a Page before the current Page.
     *
     * @return True if there is a Page before the current Page.
     */
    boolean hasPreviousPage();

    /**
     * Move to the next page and open it. If this is the last page, an IndexOutOfBoundsException will be thrown.
     *
     * @throws IndexOutOfBoundsException If the current Page is the last Page.
     */
    void nextPage() throws IndexOutOfBoundsException;

    /**
     * Move to the previous page and open it. If this is the first page, an IndexOutOfBoundsException will be thrown.
     *
     * @throws IndexOutOfBoundsException If the current Page is the first Page.
     */
    void previousPage() throws IndexOutOfBoundsException;

    /**
     * Get the number of pages this Window contains.
     *
     * @return The number of pages in this Window.
     */
    int getNumPages();

    /**
     * Get the index of the current Page.
     *
     * @return The index of the current Page.
     */
    int getCurrentPage();

    /**
     * Set the Page to the index specified. This is indexed like an array, with 0 being the first Page.
     *
     * @param index The index of the Page to set to.
     * @throws IndexOutOfBoundsException If the given index is not within the bounds of the Page list.
     */
    void setPage(int index) throws IndexOutOfBoundsException;

    /**
     * Get the page at the given index.
     *
     * @param index The index to get the Page from.
     * @return The Page at the given index.
     */
    Page getPage(int index);

    /**
     * Get the current Page.
     *
     * @return The current Page.
     */
    Page getPage();

    /**
     * Get the list of Pages this Window contains.
     *
     * @return The list of Pages this Window contains.
     */
    List<? extends Page> getPages();

    /**
     * If this Window uses the action bar to show information and has a persistent message running, update the
     * information in that message. This is used in the default implementation to show the current balance of tokens a
     * Player has, and this method is called after a purchase is made.
     */
    void updateActionBar();
}
