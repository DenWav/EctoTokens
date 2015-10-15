package com.demonwav.ectotokens.config;

import com.demonwav.ectotokens.button.Button;

/**
 * A config that defines some button. A {@link WindowConfig} defines a list of these ButtonConfigs to define which
 * buttons to show on the window. This holds the same contracts as the class it extends, {@link Config}, but also it
 * must define some implementation of {@link Button}. When adding a button to the config you must define a type tag
 * for each button that is not an {@link com.demonwav.ectotokens.config.shop.EctoButtonConfig EctoButtonConfig}, as there
 * isn't a way for the parser to know that the provided button isn't an EctoButtonConfig. You don't have to define a
 * config file to setup a button, but you must define a config if you want the user to be able to define your button in
 * their config. More information on how to do this will be added later.
 */
public interface ButtonConfig extends Config {

    /**
     * The button this ButtonConfig represents. Must not return null.
     *
     * @return The button this ButtonConfig represents.
     */
    Button getButton();
}
