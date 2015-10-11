package com.demonwav.ectotoken.config;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This defines a class which is "validatable" in some way. In the use here it is to validate classes which represent
 * configs for things. This allows each class to validate itself, allowing the config to be extendable arbitrarily.
 */
public interface Config {

    /**
     * Check if this class has been configured in a way that is valid. Return true if it is configured correctly, and
     * false if it is configured incorrectly. The choice of which components are necessary and which are optional should
     * come down to when the action this class is supposed to do is being performed, what is the minimum number of
     * components needed to work properly? Only return false for invalid configurations. The Logger is provided so you
     * can print error and warning messages to the user explaining why this failed to validate. The general contract
     * of  this method is, for any time it returns false, there will also be one or more logged messages explaining why.
     * <p/>
     * Feel free to also use the Logger to tell the user warnings that don't cause the configuration to fail. This could
     * be for configurations which are usually a sign of confusion, or useless configurations.
     * <p/>
     * For warnings, use Logger.warn(). For errors, use Logger.severe().
     *
     * @param logger The logger to print warning and error messages about this class's configuration to the user.
     * @return true if this class's configuration is valid.
     */
    boolean validate(Logger logger);

    /**
     * Validate multiple Validatable objects.
     *
     * @param logger The logger to print warning and error messages about these class' configurations to the user.
     * @param configs The classes to check configuration.
     * @return true if all supplied objects return true. False if any given objects returns false.
     */
    static boolean validate(Logger logger, Config... configs) {
        boolean result = true;
        for (Config config : configs) {
            result &= config.validate(logger);
        }
        return result;
    }

    /**
     * Validate multiple Validatable objects.
     *
     * @param logger The logger to print warning and error messages about these class' configurations to the user.
     * @param validatables The classes to check configuration.
     * @return true if all supplied objects return true. False if any given objects returns false.
     */
    static boolean validate(Logger logger, Collection<? extends Config> validatables) {
        boolean result = true;
        for (Config config : validatables) {
            result &= config.validate(logger);
        }
        return result;
    }

    static <T> List<T> removeNulls(List<T> list) {
        return list.stream().filter(l -> l != null).collect(Collectors.toList());
    }
}
