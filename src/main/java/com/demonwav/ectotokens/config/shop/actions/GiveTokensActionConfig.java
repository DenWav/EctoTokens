package com.demonwav.ectotokens.config.shop.actions;

import com.demonwav.ectotokens.action.GiveTokensAction;
import com.demonwav.ectotokens.config.ActionConfig;

import lombok.Data;

import java.util.logging.Logger;

@Data
public class GiveTokensActionConfig implements ActionConfig {

    private long amount;

    @Override
    public boolean validate(Logger logger) {
        if (amount < 0) {
            logger.severe("Give Tokens amount must be a positive integer!");
            return false;
        } else if (amount == 0) {
            logger.warning("Give Tokens action defined with 0 tokens.");
        }
        return true;
    }

    @Override
    public GiveTokensAction getAction() {
        return new GiveTokensAction(this);
    }
}
