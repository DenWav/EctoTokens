package com.demonwav.ectotokens.config.shop;

import com.demonwav.ectotokens.config.Config;
import com.demonwav.ectotokens.config.WindowConfig;

import lombok.Data;

import java.util.logging.Logger;

@Data
public class MainWindowConfig implements Config {

    private WindowConfig window;

    @Override
    public boolean validate(Logger logger) {
        return window.validate(logger);
    }
}
