package com.demonwav.ectotoken.config.shop;

import com.demonwav.ectotoken.action.Action;
import com.demonwav.ectotoken.button.EctoButton;
import com.demonwav.ectotoken.config.ActionConfig;
import com.demonwav.ectotoken.config.ButtonConfig;
import com.demonwav.ectotoken.config.Config;
import com.demonwav.ectotoken.config.shop.actions.OpenWindowActionConfig;
import com.demonwav.ectotoken.util.StringUtil;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Data
@ToString(exclude = "button")
public class EctoButtonConfig implements ButtonConfig {

    private String title;
    private List<String> desc;
    private Boolean showPrice;
    private long price;
    private int itemId;
    private short itemData;
    private String permission;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<ActionConfig> actions;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private transient EctoButton button;

    @Override
    public boolean validate(Logger logger) {
        boolean result = true;

        title = StringUtil.color(title);
        desc = Config.removeNulls(desc);
        desc = desc.stream().filter(s -> !s.trim().isEmpty()).map(StringUtil::color).collect(Collectors.toList());

        if (title.trim().isEmpty()) {
            logger.severe("Button title must be set!");
            result = false;
        }
        showPrice = showPrice == null ? false : showPrice;
        if (showPrice) {
            // TODO: implement better price view (maybe?)
            desc.add("Price: " + StringUtil.formatTokens(price));
        }

        if (actions == null || actions.isEmpty()) {
            logger.warning("Button defined with no actions does nothing.");
        } else {
            for (ActionConfig action : actions) {
                result &= action.validate(logger);
            }
        }

        // Special case
        if (actions.stream().filter(actionConfig -> actionConfig instanceof OpenWindowActionConfig).count() > 1) {
            logger.severe("Only one open window action can be supplied for each button!");
            result = false;
        }

        return result;
    }

    @Override
    public EctoButton getButton() {
        if (button == null) {
            List<Action> actionList = actions.stream().map(ActionConfig::getAction).collect(Collectors.toList());
            button = new EctoButton(title, desc, itemId, itemData, permission, price, actionList);
        }

        return button;
    }
}
