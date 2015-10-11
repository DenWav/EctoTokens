package com.demonwav.ectotoken.button;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.action.Action;
import com.demonwav.ectotoken.gui.Window;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This defines some button that can do something when clicked. For most cases the {@link EctoButton} implementation
 * will probably be powerful enough to suit your needs, as you can define your own actions to be run with it, see
 * {@link Action}. If it doesn't suit your needs, say you need access to the ItemStack that represents the button during
 * your action for some reason, then implement this class to create a button which can be used in the {@link Window}.
 * In the button list of the window simply give element of your button the tag which points to your Button
 * implementation.
 */
public interface Button {

    /**
     * The view of the Button. This serves no purpose to the window except as an icon for the button, and the text the
     * user sees as they mouse over the button.
     *
     * @return The ItemStack to use as the icon for this button.
     */
    ItemStack getItem();

    /**
     * Run when the Button is clicked.
     *
     * @param window The {@link Window} the Button is in.
     * @param player The Player that clicked the button.
     * @param plugin The EctoToken instance.
     */
    void onClick(Window window, Player player, EctoToken plugin);

    /**
     * Returns true if this player has permission to view and interact with this button.
     *
     * @param player The player to check the permission for.
     * @return True if the player has permission to view the Button
     */
    default boolean hasPermission(Player player) {
        return true;
    }
}
