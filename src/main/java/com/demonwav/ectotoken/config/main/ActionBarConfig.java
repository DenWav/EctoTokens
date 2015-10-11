package com.demonwav.ectotoken.config.main;

import com.demonwav.ectotoken.config.Config;
import com.demonwav.ectotoken.util.StringUtil;

import lombok.Data;

import java.util.logging.Logger;

@Data
public class ActionBarConfig implements Config {

    private String staticTokensView;
    private String shiftClickMessage;
    private String notEnoughTokens;
    private TokensReceived tokensReceived;

    @Override
    public boolean validate(Logger logger) {
        boolean result = true;

        staticTokensView = StringUtil.color(staticTokensView);
        shiftClickMessage = StringUtil.color(shiftClickMessage);
        notEnoughTokens = StringUtil.color(notEnoughTokens);

        if (staticTokensView == null || staticTokensView.trim().isEmpty()) {
            logger.severe("staticTokensView must be set!");
            result = false;
        }
        if (shiftClickMessage == null || shiftClickMessage.trim().isEmpty()) {
            logger.severe("shiftClickMessage must be set!");
            result = false;
        }
        if (notEnoughTokens == null || notEnoughTokens.trim().isEmpty()) {
            logger.severe("notEnoughTokens must be set!");
            result = false;
        }

        return tokensReceived.validate(logger) && result;
    }

    @Data
    public static class TokensReceived implements Config {

        private String textColor;
        private String separatorCharacter;
        private String separatorColor;
        private String tokensColor;

        @Override
        public boolean validate(Logger logger) {
            boolean result = true;

            if (textColor == null || tokensColor.trim().isEmpty()) {
                logger.severe("textColor in tokensReceived must be set!");
                result = false;
            }
            if (separatorCharacter == null || separatorCharacter.trim().isEmpty()) {
                logger.severe("separatorCharacter in tokensReceived must be set!");
                result = false;
            }
            if (separatorColor == null || separatorColor.trim().isEmpty()) {
                logger.severe("separatorColor in tokensReceived must be set!");
                result = false;
            }
            if (tokensColor == null || tokensColor.trim().isEmpty()) {
                logger.severe("tokensColor in tokensReceived must be set!");
                result = false;
            }

            return result;
        }
    }
}
