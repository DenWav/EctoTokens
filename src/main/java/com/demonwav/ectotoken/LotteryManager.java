package com.demonwav.ectotoken;

import com.demonwav.ectotoken.config.tokens.LotteryConfig;
import com.demonwav.ectotoken.querydsl.QLottery;
import com.demonwav.ectotoken.util.StringUtil;
import com.demonwav.ectotoken.util.Util;

import com.mysema.query.QueryException;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class LotteryManager {
    @Getter
    private static LotteryManager instance;

    private final EctoToken plugin;
    private final HashMap<String, LotteryConfig> configs = new HashMap<>();

    public LotteryManager(EctoToken plugin) {
        instance = this;
        this.plugin = plugin;
    }

    public void registerLottery(LotteryConfig config) {
        configs.put(config.getLotteryId(), config);
    }

    public boolean enterLottery(final Player player, long buyIn, String lotteryId) {
        if (configs.containsKey(lotteryId)) {
            // First add the buyin to the pot
            modifyCurrentPot(lotteryId, buyIn);

            LotteryConfig config = configs.get(lotteryId);
            long highestWin = 0;
            LotteryConfig.WinningOption winningOption = null;
            // Test each case
            for (LotteryConfig.WinningOption option : config.getWinningOptions()) {
                // Check if they win
                if (win(option.getChances())) {
                    if (config.getWinModeNice()) {
                        // Find the amount for this option
                        long amount = getAmountFromOption(option, lotteryId);
                        // Be nice, give the most tokens possible
                        if (highestWin < amount) {
                            highestWin = amount;
                            winningOption = option;
                        }
                    } else {
                        // Find the amount for this option
                        long amount = getAmountFromOption(option, lotteryId);
                        // Be a jerk, give as few tokens as possible
                        if (highestWin == 0) {
                            highestWin = amount;
                            winningOption = option;
                        } else if (highestWin < amount) {
                            highestWin = amount;
                            winningOption = option;
                        }
                    }
                }
            }
            if (winningOption != null) {
                // The player won something
                final long amount = getAmountFromOption(winningOption, lotteryId);

                announce(winningOption, amount, player, lotteryId);
                modifyCurrentPot(lotteryId, -1 * amount);

                long current = getCurrentPot(lotteryId);
                if (current < config.getStartingAmount()) {
                    setCurrentPot(lotteryId, config.getStartingAmount());
                }
                Util.runTask(new Runnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.BLUE + "Congratulations! You won " + ChatColor.AQUA +
                            StringUtil.formatTokens(amount) + ChatColor.BLUE + " tokens!");
                    }
                });
                TokensManager.getInstance().modifyBalance(player, amount, "LOTTERY_WIN");
            } else {
                // Player didn't win anything
                Util.runTask(new Runnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "I'm sorry, you didn't win anything.");
                    }
                });
            }
        } else {
            return false;
        }
        return true;
    }

    private long getAmountFromOption(LotteryConfig.WinningOption option, String lotteryId) {
        long amount = 0;
        switch (option.getType()) {
            case "DISCRETE":
                amount = Long.parseLong(option.getAmount());
                break;
            case "PERCENTAGE":
                amount = (long) (Double.parseDouble(option.getAmount()) * (double) getCurrentPot(lotteryId));
                break;
            case "JACKPOT":
                amount = getCurrentPot(lotteryId);
                break;
            default:
                break;
        }
        return amount;
    }

    private void announce(LotteryConfig.WinningOption option, final long winnings, final Player player, String lotteryId) {
        LotteryConfig.WinningOption.Announce announce = option.getAnnounce();
        if (announce.getEnable()) {
            final String name = player.getDisplayName();
            final long amount = TokensManager.getInstance().getBalance(player);
            final long potAfter = getCurrentPot(lotteryId) - amount;
            final String text = announce.getText();
            final String subText = announce.getSubtext();
            switch (announce.getType()) {
                case "CHAT":
                    Util.runTask(new Runnable() {
                        @Override
                        public void run() {
                            plugin.getServer().broadcastMessage(StringUtil.lotteryWinTextVar(text, name, amount, winnings, potAfter));
                        }
                    });
                    break;
                case "TITLE":
                    final String title = StringUtil.lotteryWinTextVar(text, name, amount, winnings, potAfter);
                    final String subTitle = StringUtil.lotteryWinTextVar(subText, name, amount, winnings, potAfter);
                    Util.runTask(new Runnable() {
                        @Override
                        public void run() {
                            for (Player loopPlayer : plugin.getServer().getOnlinePlayers()) {
                                loopPlayer.sendTitle(title, subTitle);
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    public long getCurrentPot(String lotteryId) {
        QLottery l = QLottery.lottery;
        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        Long result = query.from(l).where(l.lotteryId.eq(lotteryId)).uniqueResult(l.amount);
        result = result == null ? 0 : result;
        return result;
    }

    public void modifyCurrentPot(String lotteryId, long amount) {
        QLottery l = QLottery.lottery;

        try {
            SQLInsertClause clause = DatabaseManager.getInstance().getInsertClause(l);
            clause.columns(l.lotteryId, l.amount).values(lotteryId, amount).execute();
        } catch (QueryException e) {
            SQLUpdateClause clause = DatabaseManager.getInstance().getUpdateClause(l);
            clause.where(l.lotteryId.eq(lotteryId)).set(l.amount, l.amount.add(amount)).execute();
        }
    }

    public void setCurrentPot(String lotteryId, long amount) {
        QLottery l = QLottery.lottery;

        try {
            SQLInsertClause clause = DatabaseManager.getInstance().getInsertClause(l);
            clause.columns(l.lotteryId, l.amount).values(lotteryId, amount).execute();
        } catch (QueryException e) {
            SQLUpdateClause clause = DatabaseManager.getInstance().getUpdateClause(l);
            clause.where(l.lotteryId.eq(lotteryId)).set(l.amount, amount).execute();
        }
    }

    private boolean win(double prob) {
        return Math.random() < prob;
    }
}
