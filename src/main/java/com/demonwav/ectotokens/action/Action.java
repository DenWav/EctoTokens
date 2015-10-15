package com.demonwav.ectotokens.action;

import com.demonwav.ectotokens.EctoTokens;
import com.demonwav.ectotokens.button.Button;
import com.demonwav.ectotokens.button.EctoButton;
import com.demonwav.ectotokens.config.ActionConfig;
import com.demonwav.ectotokens.gui.Window;

import org.bukkit.entity.Player;

// TODO: update when extension documentation is available
/**
 * The basic action that a general button will run. This creates a contract with the implementing class that it:
 * <pre>
 *     1. Has an action it can perform when the Button that owns this Action is clicked
 *     2. Can optionally check the current state of the player/server to determine if the Action would be successful
 * </pre>
 * This is so arbitrary actions can be defined without the window manager needing to know anything about
 * it. This also allows {@link EctoButton general buttons} to run arbitrary actions, and to have their list of
 * actions be extendable by other plugins by simply implementing this interface.
 * <p>
 * To create your own actions, implement this class in your plugin. Then implement the {@link ActionConfig} class which
 * define your Action implementation. Have the user add the ActionConfig to their shop.yml file to enable the action.
 * More in-depth documentation on how this is done will be created later.
 * <p>
 * This is the preferred method of extending functionality of the GUI. However, if you need to define the button itself,
 * look {@link Button here}.
 */
public abstract class Action {

    /**
     * The action to be run when this item has been clicked. The contract of this method is it is run on the main thread
     * and may access Bukkit API's freely. It may take any action, however long operations should be run asynchronously
     * to avoid lockups.
     *
     * @param window The Window that owns the button which runs this action.
     * @param player The Player which clicked the button.
     * @param plugin The instance of the EctoToken plugin which is running.
     */
    public abstract void run(Window window, Player player, EctoTokens plugin);

    /**
     * If there is a chance your Action could fail, override this method. The contract of this method is that it must
     * be read-only. It is simply to check the current state of whatever it needs to modify and return true or false
     * depending if at this current state the Action can be performed completely. This method is run before the
     * {@link #run(Window, Player, EctoTokens) run()} method, and if any of the Actions return false from this method,
     * the Action will be canceled. If your action is not failable then there is no need to override this method, as by
     * default it simply returns true.
     *
     * @param window The Window that owns the button which runs this action.
     * @param player The Player which clicked the button.
     * @param plugin The instance of the EctoToken plugin which is running.
     * @return true if the Action will not fail in the current state of whatever this Action is modifying.
     */
    public boolean checkAction(Window window, Player player, EctoTokens plugin) {
        return true;
    }
}
