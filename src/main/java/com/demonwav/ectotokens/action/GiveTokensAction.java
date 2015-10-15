package com.demonwav.ectotokens.action;

import com.demonwav.ectotokens.EctoTokens;
import com.demonwav.ectotokens.TokensManager;
import com.demonwav.ectotokens.config.shop.actions.GiveTokensActionConfig;
import com.demonwav.ectotokens.gui.Window;

import org.bukkit.entity.Player;

public class GiveTokensAction extends Action {

    private final GiveTokensActionConfig config;

    public GiveTokensAction(GiveTokensActionConfig config) {
        this.config = config;
    }

    @Override
    public void run(Window window, Player player, EctoTokens plugin) {
        TokensManager.getInstance().modifyBalance(player, config.getAmount(), "GIVE_TOKENS_ACTION");
        window.updateActionBar();
    }
}
