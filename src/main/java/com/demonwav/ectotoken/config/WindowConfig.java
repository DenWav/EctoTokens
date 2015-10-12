package com.demonwav.ectotoken.config;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.gui.Window;

import org.bukkit.entity.Player;

// TODO: update this when extending documentation is available
/**
 * A config that defines some window. The {@link com.demonwav.ectotoken.config.shop.MainWindowConfig MainWindowConfig}
 * defines a root window which all windows spawn from. However,
 * {@link com.demonwav.ectotoken.config.shop.actions.OpenWindowActionConfig OpenWindowActionConfig} can define
 * other implementations of this class. This holds the same contracts as the class it extends, {@link Config}, but also
 * it must define some implementation of {@link Window}. When adding a window to the config you must define a type tag
 * for each window that is not an {@link com.demonwav.ectotoken.config.shop.EctoWindowConfig EctoWindowConfg}, as the
 * parser would have no way of knowing this implementation is not an EctoWindowConfig. You don't have to define a config
 * file to setup a window, but you must define a config if you want the user to be able to define your window in their
 * config. More information on how to do this will be added later.
 */
public interface WindowConfig extends Config {

    /**
     * Return the title for the Window represented by this config. Must not return null.
     *
     * @return The title for the Window represented by this config.
     */
    String getTitle();

    /**
     * The Window this WindowConfig represents. Must not return null.
     *
     * @param height The global setting for how many rows the Window should have.
     * @param parent The parent Window of this Window, can be null if this is the root Window
     * @param player The player to open this window for
     * @param plugin The instance of the EctoToken plugin
     * @return The Window this WindowConfig represents.
     */
    Window getWindow(int height, Window parent, Player player, EctoToken plugin);
}
