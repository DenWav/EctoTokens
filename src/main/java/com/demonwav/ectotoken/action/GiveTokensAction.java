package com.demonwav.ectotoken.action;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.TokensManager;
import com.demonwav.ectotoken.config.shop.actions.GiveTokensActionConfig;
import com.demonwav.ectotoken.gui.Window;

import org.bukkit.entity.Player;

public class GiveTokensAction implements Action {

    private final GiveTokensActionConfig config;

    public GiveTokensAction(GiveTokensActionConfig config) {
        this.config = config;
    }

    @Override
    public void run(Window window, Player player, EctoToken plugin) {
        TokensManager.getInstance().modifyBalance(player, config.getAmount(), "GIVE_TOKENS_ACTION");
        window.updateActionBar();
    }
}
