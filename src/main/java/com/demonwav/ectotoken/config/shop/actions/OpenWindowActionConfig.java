package com.demonwav.ectotoken.config.shop.actions;

import com.demonwav.ectotoken.action.OpenWindowAction;
import com.demonwav.ectotoken.config.ActionConfig;
import com.demonwav.ectotoken.config.WindowConfig;

import lombok.Data;

import java.util.logging.Logger;

@Data
public class OpenWindowActionConfig implements ActionConfig {

    private WindowConfig window;

    @Override
    public boolean validate(Logger logger) {
        return window.validate(logger);
    }

    @Override
    public OpenWindowAction getAction() {
        return new OpenWindowAction(this);
    }
}
