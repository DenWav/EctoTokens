package com.demonwav.ectotoken.config.main.nav;

import com.demonwav.ectotoken.config.ButtonConfig;
import com.demonwav.ectotoken.config.Config;
import com.demonwav.ectotoken.util.StringUtil;

import lombok.Data;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Data
public abstract class NavButtonConfig implements ButtonConfig {

    private int itemId;
    private short itemData;
    private String title;
    private List<String> desc;

    @Override
    public boolean validate(Logger logger) {
        title = StringUtil.color(title);
        desc = Config.removeNulls(desc);
        desc = desc.stream().filter(s -> !s.trim().isEmpty()).map(StringUtil::color).collect(Collectors.toList());

        if (title.trim().isEmpty()) {
            logger.severe("Nav button title must be set!");
            return false;
        }
        return true;
    }
}
