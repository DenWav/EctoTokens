package com.demonwav.ectotoken.config.main;

import com.demonwav.ectotoken.config.Config;
import com.demonwav.ectotoken.util.StringUtil;

import lombok.Data;

import java.util.logging.Logger;

@Data
public class CommandsConfig implements Config {

    private HelpConfig help;
    private String balance;
    private TopConfig top;

    @Override
    public boolean validate(Logger logger) {
        boolean result = help.validate(logger);
        result &= top.validate(logger);

        if (balance == null || balance.trim().isEmpty()) {
            logger.severe("balance must be set in commands!");
            result = false;
        }

        balance = StringUtil.color(balance);

        return result;
    }

    @Data
    public static class HelpConfig implements Config {

        private String playerHelp;
        private String adminHelp;

        @Override
        public boolean validate(Logger logger) {
            boolean result = true;

            if (playerHelp == null || playerHelp.trim().isEmpty()) {
                logger.severe("playerHelp must be set!");
                result = false;
            }
            if (adminHelp == null || adminHelp.trim().isEmpty()) {
                logger.severe("adminHelp must be set!");
                result = false;
            }

            playerHelp = StringUtil.color(playerHelp);
            adminHelp = StringUtil.color(adminHelp);

            return result;
        }
    }

    @Data
    public static class TopConfig implements Config {

        private String header;
        private String lines;

        @Override
        public boolean validate(Logger logger) {
            boolean result = true;

            if (header == null || header.trim().isEmpty()) {
                logger.severe("header must be set in top!");
                result = false;
            }
            if (lines == null || lines.trim().isEmpty()) {
                logger.severe("lines must be set in top!");
                result = false;
            }

            header = StringUtil.color(header);
            lines = StringUtil.color(lines);

            return result;
        }
    }
}
