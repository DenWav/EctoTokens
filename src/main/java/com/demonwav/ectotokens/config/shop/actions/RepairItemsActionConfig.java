package com.demonwav.ectotokens.config.shop.actions;

import com.demonwav.ectotokens.action.RepairItemsAction;
import com.demonwav.ectotokens.config.ActionConfig;
import com.demonwav.ectotokens.config.Configs;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Data
public class RepairItemsActionConfig implements ActionConfig {

    private Boolean onlyEquippedItems;
    private List<Integer> itemTypes;
    private int cost;


    @Override
    public boolean validate(Logger logger) {
        onlyEquippedItems = onlyEquippedItems == null ? false : onlyEquippedItems;

        if (itemTypes == null)
            itemTypes = Collections.emptyList();

        Configs.removeNulls(itemTypes);

        if (cost < 0) {
            logger.severe("Repair cost must not be negative!");
            return false;
        }

        return true;
    }

    @Override
    public RepairItemsAction getAction() {
        return new RepairItemsAction(this);
    }
}
