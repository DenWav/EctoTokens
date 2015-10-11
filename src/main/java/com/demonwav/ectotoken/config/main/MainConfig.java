package com.demonwav.ectotoken.config.main;

import com.demonwav.ectotoken.config.Config;

import lombok.Data;

import java.util.logging.Logger;

@Data
public class MainConfig implements Config {

    private MySQLConfig mysql;
    private GuiConfig gui;
    private CommandsConfig commands;

    @Override
    public boolean validate(Logger logger) {
        // Do it this way so it doesn't short circuit the rest of the logic expression on the first false
        // we want this so the user will see everything that's wrong with the config
        boolean result = mysql.validate(logger);
        result &= gui.validate(logger);
        result &= commands.validate(logger);
        return result;
    }
}
