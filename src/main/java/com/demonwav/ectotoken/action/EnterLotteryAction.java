package com.demonwav.ectotoken.action;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.LotteryManager;
import com.demonwav.ectotoken.config.shop.actions.EnterLotteryActionConfig;
import com.demonwav.ectotoken.gui.Window;

import org.bukkit.entity.Player;

public class EnterLotteryAction implements Action {

    private final EnterLotteryActionConfig config;

    public EnterLotteryAction(EnterLotteryActionConfig config) {
        this.config = config;
    }

    @Override
    public void run(Window window, Player player, EctoToken plugin) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () ->
            LotteryManager.getInstance().enterLottery(player, config.getBuyIn(), config.getLotteryId())
        );
        plugin.getServer().getScheduler().runTaskLater(plugin, window::updateActionBar, 10);
    }
}
