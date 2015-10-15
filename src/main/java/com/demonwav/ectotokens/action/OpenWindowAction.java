package com.demonwav.ectotokens.action;

import com.demonwav.ectotokens.EctoTokens;
import com.demonwav.ectotokens.config.shop.actions.OpenWindowActionConfig;
import com.demonwav.ectotokens.gui.Window;

import org.bukkit.entity.Player;

public class OpenWindowAction extends Action {

    private final OpenWindowActionConfig config;

    public OpenWindowAction(OpenWindowActionConfig config) {
        this.config = config;
    }

    @Override
    public void run(Window window, Player player, EctoTokens plugin) {
        int height = plugin.getMainConfig().getGui().getHeight();
        Window newWindow = config.getWindow().getWindow(height, window, player, plugin);
        window.close();
        newWindow.createWindow();
    }
}
