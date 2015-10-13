package com.demonwav.ectotoken.config.main.nav;

import com.demonwav.ectotoken.config.ButtonConfig;
import com.demonwav.ectotoken.config.Configs;
import com.demonwav.ectotoken.util.StringUtil;

import lombok.Data;

import java.util.List;
import java.util.logging.Logger;

@Data
public abstract class NavButtonConfig implements ButtonConfig {

    private int itemId;
    private short itemData;
    private String title;
    private List<String> desc;

    @Override
    public boolean validate(Logger logger) {
        title = StringUtil.color(title);

        Configs.removeEmptiesAndColor(desc);

        if (title.trim().isEmpty()) {
            logger.severe("Nav button title must be set!");
            return false;
        }
        return true;
    }
}
