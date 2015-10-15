package com.demonwav.ectotokens.config.shop;

import com.demonwav.ectotokens.action.Action;
import com.demonwav.ectotokens.button.EctoButton;
import com.demonwav.ectotokens.config.ActionConfig;
import com.demonwav.ectotokens.config.ButtonConfig;
import com.demonwav.ectotokens.config.Configs;
import com.demonwav.ectotokens.config.shop.actions.OpenWindowActionConfig;
import com.demonwav.ectotokens.util.StringUtil;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
        Configs.removeEmptiesAndColor(desc);

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
        int count = 0;
        for (ActionConfig action : actions)
            if (action instanceof OpenWindowActionConfig)
                count++;
        if (count > 1) {
            logger.severe("Only one open window action can be supplied for each button!");
            result = false;
        }

        return result;
    }

    @Override
    public EctoButton getButton() {
        if (button == null) {
            List<Action> actionList = new ArrayList<>();
            for (ActionConfig action : actions)
                actionList.add(action.getAction());
            button = new EctoButton(title, desc, itemId, itemData, permission, price, actionList);
        }

        return button;
    }
}
