package com.demonwav.ectotoken.config.shop;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.button.Button;
import com.demonwav.ectotoken.config.ButtonConfig;
import com.demonwav.ectotoken.config.Configs;
import com.demonwav.ectotoken.config.WindowConfig;
import com.demonwav.ectotoken.gui.EctoWindow;
import com.demonwav.ectotoken.gui.Window;
import com.demonwav.ectotoken.util.StringUtil;

import lombok.Data;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Data
public class EctoWindowConfig implements WindowConfig {

    private String title;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<ButtonConfig> buttons;

    @Override
    public boolean validate(Logger logger) {
        boolean result = true;

        title = StringUtil.color(title);

        if (title == null || title.trim().isEmpty()) {
            logger.severe("Window title must be set!");
            result = false;
        }

        Configs.removeNulls(buttons);

        result &= Configs.validate(logger, buttons);

        return result;
    }

    @Override
    public Window getWindow(int height, Window parent, Player player, EctoToken plugin) {
        List<Button> buttonList = new ArrayList<>();
        for (ButtonConfig button : buttons)
            if (button.getButton().hasPermission(player))
                buttonList.add(button.getButton());
        return new EctoWindow(getTitle(), height, buttonList, parent, player, plugin);
    }
}
