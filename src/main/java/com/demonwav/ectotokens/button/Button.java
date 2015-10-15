package com.demonwav.ectotokens.button;

import com.demonwav.ectotokens.EctoTokens;
import com.demonwav.ectotokens.action.Action;
import com.demonwav.ectotokens.config.ButtonConfig;
import com.demonwav.ectotokens.gui.Window;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

// TODO: update this when extending documentation is available
/**
 * This defines some Button that can do something when clicked. For most cases the {@link EctoButton} implementation
 * will probably be powerful enough to suit your needs, as you can define your own {@link Action actions} to be run with
 * it. If it doesn't suit your needs, say you need access to the ItemStack that represents the button during
 * your action for some reason, then implement this class to create a button which can be used in the {@link Window}.
 * To extend this in your own plugin, implement {@link ButtonConfig} and have the user add teh entry to the button list
 * in the window section of shop.yml. You will need to add the class tag for the Button, as it won't be the
 * {@link EctoButton} implementation the parser is expecting. More information on how to do this will be added later.
 */
public abstract class Button {

    /**
     * The view of the Button. This serves no purpose to the Window except as an icon for the button, and the text the
     * user sees as they mouse over the button.
     *
     * @return The ItemStack to use as the icon for this button.
     */
    public abstract ItemStack getItem();

    /**
     * Run when the Button is clicked.
     *
     * @param window The {@link Window} the Button is in.
     * @param player The Player that clicked the button.
     * @param plugin The EctoToken instance.
     */
    public abstract void onClick(Window window, Player player, EctoTokens plugin);

    /**
     * Returns true if this player has permission to view and interact with this button.
     *
     * @param player The player to check the permission for.
     * @return True if the player has permission to view the Button
     */
    public boolean hasPermission(Player player) {
        return true;
    }
}
