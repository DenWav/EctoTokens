package com.demonwav.ectotokens.config.tokens;

import com.demonwav.ectotokens.LotteryManager;
import com.demonwav.ectotokens.config.Config;
import com.demonwav.ectotokens.config.Configs;
import com.demonwav.ectotokens.util.StringUtil;

import lombok.Data;

import java.util.List;
import java.util.logging.Logger;

@Data
public class LotteryConfig implements Config {

    private String lotteryId;
    private long startingAmount;
    private Boolean winModeNice;
    private List<WinningOption> winningOptions;

    @Override
    public boolean validate(Logger logger) {
        boolean result = true;
        if (lotteryId == null || lotteryId.trim().isEmpty()) {
            logger.severe("Lottery ID must be set!");
            result = false;
        }
        if (startingAmount < 0) {
            logger.severe("Starting lottery amount must not be negative!");
            result = false;
        } else if (startingAmount == 0) {
            logger.warning("Lottery defined with starting amount at 0.");
        }

        Configs.removeNulls(winningOptions);

        winModeNice = winModeNice == null ? false : winModeNice;
        if (winningOptions == null || winningOptions.isEmpty()) {
            logger.severe("Lottery defined with 0 winning options!");
            result = false;
        } else {
            result &= Configs.validate(logger, winningOptions);
        }

        LotteryManager.getInstance().registerLottery(this);

        return result;
    }

    @Data
    public static class WinningOption implements Config {

        private String type;
        private String amount;
        private double chances;
        private Announce announce;

        @Override
        public boolean validate(Logger logger) {
            boolean result = true;
            if (type == null || type.trim().isEmpty()) {
                logger.severe("Winning option type must be set!");
                result = false;
            } else {
                switch (type.toUpperCase()) {
                    case "DISCRETE":
                        try {
                            int i = Integer.parseInt(amount);
                            if (i < 1) {
                                logger.severe("DISCRETE winning option must have a non-zero positive integer amount value!");
                                result = false;
                            }
                        } catch (NumberFormatException e) {
                            logger.severe("DISCRETE winning option must have an integer amount!");
                            result = false;
                        }
                        break;
                    case "PERCENTAGE":
                        try {
                            double d = Double.parseDouble(amount);
                            if (d < 0 || d > 1) {
                                logger.severe("PERCENTAGE winning option must have an amount value between 0 and 1!");
                                result = false;
                            } else if (d == 0) {
                                logger.warning("PERCENTAGE winning option set with an amount of 0, no tokens will be awarded.");
                            } else if (d == 1) {
                                logger.warning("PERCENTAGE winning option set with an amount of 1, this is the same as JACKPOT.");
                            }
                        } catch (NumberFormatException e) {
                            logger.severe("PERCENTAGE winning option must have a decimal amount!");
                            result = false;
                        }
                        break;
                    case "JACKPOT":
                        break;
                    default:
                        logger.severe(type + " is not a valid winning option!");
                        result = false;
                        break;
                }
            }
            if (chances < 0 || chances > 1) {
                logger.severe("chances value in winning option must be between 0 and 1!");
                result = false;
            } else if (chances == 1) {
                logger.warning("chances value in winning option set to 1, this is a certain win.");
            } else if (chances == 0) {
                logger.warning("chances value in winning option set to 0, this is an impossible win.");
            }
            return result && announce.validate(logger);
        }

        @Data
        public static class Announce implements Config {

            private Boolean enable;
            private String type;
            private String text;
            private String subtext;

            @Override
            public boolean validate(Logger logger) {
                enable = enable == null ? false : enable;

                text = StringUtil.color(text);
                subtext = StringUtil.color(subtext);

                boolean result = true;
                if (enable) {
                    switch (type.toUpperCase()) {
                        case "CHAT":
                            if (subtext != null && !subtext.isEmpty()) {
                                logger.warning("announce type in winning option set to CHAT with a useless subtext is defined.");
                            }
                            break;
                        case "TITLE":
                            // Nothing to check here
                            break;
                        default:
                            logger.severe("type field in announce field in winning option must be CHAT or TITLE!");
                            result = false;
                            break;
                    }
                    if (text == null || text.isEmpty()) {
                        logger.severe("Text in announce field of winning option must be set!");
                        result = false;
                    }
                }
                return result;
            }
        }
    }
}
