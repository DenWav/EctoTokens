package com.demonwav.ectotoken.config;

import com.demonwav.ectotoken.util.StringUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class Configs {

    /**
     * Validate multiple Config objects.
     *
     * @param logger  The logger to print warning and error messages about these class' configurations to the user.
     * @param configs The classes to check configuration.
     *
     * @return true if all supplied objects return true. False if any given objects returns false.
     */
    public static boolean validate(Logger logger, Config... configs) {
        boolean result = true;
        for (Config config : configs) {
            result &= config.validate(logger);
        }
        return result;
    }

    /**
     * Validate multiple Config objects.
     *
     * @param logger  The logger to print warning and error messages about these class' configurations to the user.
     * @param configs The classes to check configuration.
     *
     * @return true if all supplied objects return true. False if any given objects returns false.
     */
    public static boolean validate(Logger logger, Collection<? extends Config> configs) {
        boolean result = true;
        for (Config config : configs) {
            result &= config.validate(logger);
        }
        return result;
    }

    /**
     * The parser will initialize lists with no entries as a list of length one with a null reference. In the validate()
     * method use this convenience to remove null values from your lists.
     *
     * @param list The list to check for null values.
     * @param <T> The type encapsulated by the list.
     */
    public static <T> void removeNulls(List<T> list) {
        list.removeAll(Collections.<T>singletonList(null));
    }

    /**
     * Remove null and empty string values from a list, and replace teh & character with the § character for colors.
     *
     * @param list The list to clean up.
     */
    public static void removeEmptiesAndColor(List<String> list) {
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            if (s == null || s.trim().isEmpty())
                iterator.remove();
        }
        for (int i = 0; i < list.size(); i++) {
            list.set(i, StringUtil.color(list.get(i)));
        }
    }

}
