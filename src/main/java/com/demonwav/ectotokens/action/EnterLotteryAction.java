package com.demonwav.ectotokens.action;

import com.demonwav.ectotokens.EctoTokens;
import com.demonwav.ectotokens.LotteryManager;
import com.demonwav.ectotokens.config.shop.actions.EnterLotteryActionConfig;
import com.demonwav.ectotokens.gui.Window;
import com.demonwav.ectotokens.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EnterLotteryAction extends Action {

    private final EnterLotteryActionConfig config;

    public EnterLotteryAction(EnterLotteryActionConfig config) {
        this.config = config;
    }

    @Override
    public void run(final Window window, final Player player, final EctoTokens plugin) {
        Util.runTaskAsync(new Runnable() {
            @Override
            public void run() {
                LotteryManager.getInstance().enterLottery(player, config.getBuyIn(), config.getLotteryId());
            }
        });
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                window.updateInformation();
            }
        }, 10);
    }
}
