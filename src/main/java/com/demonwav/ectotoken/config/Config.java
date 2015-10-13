package com.demonwav.ectotoken.config;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * This defines a class which is "validatable" in some way. It is to validate classes which represent
 * configs for things. This allows each class to validate itself, allowing the config to be extendable arbitrarily.
 * <p>
 * All Config implementations must be standard JavaBeans with a public no-argument constructor. All properties must be
 * private with getters and setters for each. Setting each property as private and using the {@code Data} annotation
 * from Lombok is sufficient. Any property of a Config which is not a Map, List, String, Boolean, or primitive type
 * and does not have the {@code transient} flag must also implement this class. Primitive value types are fine, but for
 * boolean values, use the Boolean wrapper class instead. This is because this plugin uses YamlBeans and for some reason
 * it doesn't understand primitive boolean values (probably because the JavaBean spec for boolean getters is {@code is}
 * rather than {@code get}, so make sure you use {@code getBoolean} even for your Boolean values). If you implement your
 * own getter for a primitive boolean type that uses the {@code get} prefix rather than {@code is} it may work, but I
 * have not tested it.
 */
public interface Config extends Serializable {

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
}
