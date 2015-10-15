package com.demonwav.ectotokens.config.main;

import com.demonwav.ectotokens.config.Config;
import com.demonwav.ectotokens.config.Configs;
import com.demonwav.ectotokens.config.main.nav.EctoBackButtonConfig;
import com.demonwav.ectotokens.config.main.nav.EctoCloseButtonConfig;
import com.demonwav.ectotokens.config.main.nav.EctoLeftNavButtonConfig;
import com.demonwav.ectotokens.config.main.nav.EctoRightNavButtonConfig;

import lombok.Data;

import java.util.logging.Logger;

@Data
public class GuiConfig implements Config {

    private EctoLeftNavButtonConfig leftNavButton;
    private EctoRightNavButtonConfig rightNavButton;
    private EctoBackButtonConfig backButton;
    private EctoCloseButtonConfig closeButton;
    private int height;
    private ActionBarConfig actionBar;

    @Override
    public boolean validate(Logger logger) {
        boolean result = Configs.validate(logger, leftNavButton, rightNavButton, backButton, closeButton);

        if (height < 1 || height > 6) {
            logger.severe("Height must be greater than 0 and less than 6.");
            result = false;
        }

        result &= actionBar.validate(logger);
        return result;
    }
}
