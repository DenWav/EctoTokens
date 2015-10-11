package com.demonwav.ectotoken.config.shop.actions;

import com.demonwav.ectotoken.action.EnterLotteryAction;
import com.demonwav.ectotoken.config.ActionConfig;

import lombok.Data;

import java.util.logging.Logger;

@Data
public class EnterLotteryActionConfig implements ActionConfig {

    private String lotteryId;
    private long buyIn;

    @Override
    public boolean validate(Logger logger) {
        boolean result = true;
        if (lotteryId == null || lotteryId.trim().isEmpty()) {
            logger.severe("Lottery ID must be set!");
            result = false;
        }
        if (buyIn < 0) {
            logger.severe("Buyin for lottery must not be negative!");
            result = false;
        } else if (buyIn == 0) {
            logger.warning("0 buyin for lottery means the lottery will never grow.");
        }
        return result;
    }

    @Override
    public EnterLotteryAction getAction() {
        return new EnterLotteryAction(this);
    }
}
