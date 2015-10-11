package com.demonwav.ectotoken.action;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.button.Button;
import com.demonwav.ectotoken.config.shop.EctoButtonConfig;
import com.demonwav.ectotoken.gui.Window;

import org.bukkit.entity.Player;

/**
 * The basic action that a general button will run. This is simply a composite interface to combine the behavior of
 * two interfaces to create a contract on the implementing class that it:
 * <pre>
 *     1. Has an action it can perform when the button that owns this Action is clicked
 *     2. Can validate the config given to it by the Yaml parser
 * </pre>
 * This is so arbitrary actions can be defined in the config without the window manager needing to know anything about
 * it. This also allows {@link EctoButtonConfig general buttons} to run arbitrary actions, and to have their list of
 * actions be extendable by other plugins by simply adding it to the config.
 * <p/>
 * To create your own actions, implement this class in your plugin. Then have the user of this plugin add an action to
 * one of the buttons in their window (or create a new button if desired) and specify an action with a type tag which
 * points to your class.
 * <p/>
 * This is the preferred method of extending functionality of the GUI. However, if you need to define the button itself,
 * look {@link Button here}.
 */
public interface Action {

    /**
     * The action to be run when this item has been clicked. The contract of this method is it is run on the main thread
     * and may access Bukkit API's freely. It may take any action, however long operations should be run asynchronously
     * to avoid lockups.
     *
     * @param window The EctoGuiWindow that owns the button which runs this action
     * @param player The player which clicked the button
     * @param plugin The instance of the EctoToken plugin which is running
     * @return false if this Action failed, true otherwise
     */
    void run(Window window, Player player, EctoToken plugin);

    default boolean checkAction(Window window, Player player, EctoToken plugin) {
        return true;
    }
}
