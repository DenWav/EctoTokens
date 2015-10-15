package com.demonwav.ectotokens.config.tokens;

import com.demonwav.ectotokens.config.Config;

import lombok.Data;

import java.util.logging.Logger;

@Data
public class OreConfig implements Config {

    private int itemId;
    private short itemData;
    private long tokensAwarded;

    @Override
    public boolean validate(Logger logger) {
        if (tokensAwarded < 0) {
            logger.severe("Tokens awarded from mining ore must not be negative!");
            return false;
        } else if (tokensAwarded == 0) {
            logger.warning("Ore listing for " + itemId + ":" + itemData + " gives 0 tokens.");
        }
        return true;
    }
}
