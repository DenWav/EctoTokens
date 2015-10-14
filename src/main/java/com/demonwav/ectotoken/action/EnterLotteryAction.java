package com.demonwav.ectotoken.action;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.LotteryManager;
import com.demonwav.ectotoken.config.shop.actions.EnterLotteryActionConfig;
import com.demonwav.ectotoken.gui.Window;
import com.demonwav.ectotoken.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EnterLotteryAction extends Action {

    private final EnterLotteryActionConfig config;

    public EnterLotteryAction(EnterLotteryActionConfig config) {
        this.config = config;
    }

    @Override
    public void run(final Window window, final Player player, final EctoToken plugin) {
        Util.runTaskAsync(new Runnable() {
            @Override
            public void run() {
                LotteryManager.getInstance().enterLottery(player, config.getBuyIn(), config.getLotteryId());
            }
        });
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                window.updateActionBar();
            }
        }, 10);
    }
}
