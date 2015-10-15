package com.demonwav.ectotokens.config.tokens;

import com.demonwav.ectotokens.config.Config;
import com.demonwav.ectotokens.config.Configs;

import lombok.Data;

import java.util.List;
import java.util.logging.Logger;

@Data
public class TokensConfig implements Config {

    private MobsConfig mobs;
    private List<OreConfig> ores;
    private List<LotteryConfig> lotteries;

    @Override
    public boolean validate(Logger logger) {
        boolean result = mobs.validate(logger);
        if (ores != null) {
            result &= Configs.validate(logger, ores);
        }

        Configs.removeNulls(lotteries);

        if (lotteries != null) {
            result &= Configs.validate(logger, lotteries);
        }
        return result;
    }
}
