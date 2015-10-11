package com.demonwav.ectotoken.util;

import com.demonwav.ectotoken.config.main.ActionBarConfig;

import java.text.DecimalFormat;

public class StringUtil {

    private static final DecimalFormat format = new DecimalFormat("#,###");

    public static String staticTokensViewVar(String s, String name, long tokens) {
        return color(s.replaceAll("\\{tokencount\\}", formatTokens(tokens)).replaceAll("\\{playername\\}", name));
    }

    public static String notEnoughTokensVar(String s, String name, long tokens, int price) {
        long tokensNeeded = price - tokens;

        return s.replaceAll("\\{playername\\}", name)
                .replaceAll("\\{tokencount\\}", formatTokens(tokens))
                .replaceAll("\\{pricevalue\\}", formatTokens(price))
                .replaceAll("\\{tokensneed\\}", formatTokens(tokensNeeded));
    }

    public static String windowTitleVar(String s, String name, int p, int t) {
        return s.replaceAll("\\{pagenumber\\}", String.valueOf(p))
                .replaceAll("\\{totalpages\\}", String.valueOf(t))
                .replaceAll("\\{playername\\}", name);
    }

    public static String lotteryWinTextVar(String s, String name, long tokens, long winnings, long newPot) {
        long potBefore = newPot + winnings;

        return s.replaceAll("\\{playername\\}", name)
                .replaceAll("\\{winnings\\}", formatTokens(winnings))
                .replaceAll("\\{tokencount\\}", formatTokens(tokens))
                .replaceAll("\\{potafter\\}", formatTokens(newPot))
                .replaceAll("\\{potbefore\\}", formatTokens(potBefore));
    }

    public static String tokensReceivedTextVar(String desc, long amount, ActionBarConfig.TokensReceived config) {
        return "§" + config.getTextColor() + desc + " §" + config.getSeparatorColor() + config.getSeparatorCharacter()
            + " §" + config.getTokensColor() + formatTokens(amount);
    }

    public static String balanceTextVar(String s, String name, long tokens) {
        return s.replaceAll("\\{playername\\}", name)
                .replaceAll("\\{tokencount\\}", formatTokens(tokens));
    }

    public static String topHeaderTextVar(String s, long total) {
        return s.replaceAll("\\{totaltokens\\}", formatTokens(total));
    }

    public static String topLineTextHeader(String s, String name, long amount, int line) {
        return s.replaceAll("\\{linenumber\\}", String.valueOf(line))
                .replaceAll("\\{playername\\}", name)
                .replaceAll("\\{tokencount\\}", formatTokens(amount));
    }

    public static String color(String s) {
        // lol
        return s.replaceAll("&", "§").replaceAll("§§", "&");
    }

    public static String formatTokens(long tokens) {
        return format.format(tokens);
    }

    public static String red(String text) {
        return "\u001b[1;31m" + text + "\u001b[m";
    }
}
