package com.demonwav.ectotoken.action;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.config.shop.actions.OpenWindowActionConfig;
import com.demonwav.ectotoken.gui.Window;

import org.bukkit.entity.Player;

public class OpenWindowAction implements Action {

    private final OpenWindowActionConfig config;

    public OpenWindowAction(OpenWindowActionConfig config) {
        this.config = config;
    }

    @Override
    public void run(Window window, Player player, EctoToken plugin) {
        int height = plugin.getMainConfig().getGui().getHeight();
        Window newWindow = config.getWindow().getWindow(height, window, player, plugin);
        window.close();
        newWindow.createWindow();
    }
}
