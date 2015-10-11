package com.demonwav.ectotoken.config.shop;

import com.demonwav.ectotoken.config.Config;
import com.demonwav.ectotoken.config.WindowConfig;

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
