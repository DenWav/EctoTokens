package com.demonwav.ectotokens.config;

import com.demonwav.ectotokens.action.Action;

// TODO: update this when extending documentation is available
/**
 * A config that defines some action. A {@link ButtonConfig} defines a list of these ActionConfigs to define what the
 * button does. This holds the same contracts as the class it extends, {@link Config}, but also it must define some
 * implementation of {@link Action}. When adding an action to the config you must define a type tag for each action,
 * as the parser has no way of knowing which implementation to use. You don't have to define a config file to setup an
 * action on some button, but you must define a config if you want the user to be able to define your action in their
 * config. More information on how to do this will be added later.
 */
public interface ActionConfig extends Config {

    /**
     * The action this ActionConfig represents. Must not return null.
     *
     * @return The action this ActionConfig represents.
     */
    Action getAction();
}
