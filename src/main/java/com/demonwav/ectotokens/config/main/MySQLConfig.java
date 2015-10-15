package com.demonwav.ectotokens.config.main;

import com.demonwav.ectotokens.config.Config;

import lombok.Data;

import java.util.logging.Logger;

@Data
public class MySQLConfig implements Config {

    private String hostname;
    private String username;
    private String password;
    private String database;
    private String port;
    private Boolean forceSetup;

    @Override
    public boolean validate(Logger logger) {
        forceSetup = forceSetup == null ? false : forceSetup;
        boolean result = true;
        if (hostname == null || hostname.trim().isEmpty()) {
            logger.severe("MySQL hostname must be set!");
            result = false;
        }
        if (username == null || username.trim().isEmpty()) {
            logger.severe("MySQL username must be set!");
            result = false;
        }
        if (database == null || database.trim().isEmpty()) {
            logger.severe("MySQL database name must be set!");
            result = false;
        }
        if (port == null || port.trim().isEmpty()) {
            logger.severe("MySQL port must be set!");
            result = false;
        }
        return result;
    }
}
