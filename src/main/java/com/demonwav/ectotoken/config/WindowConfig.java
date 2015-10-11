package com.demonwav.ectotoken.config;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.gui.Window;

import org.bukkit.entity.Player;

public interface WindowConfig extends Config {

    String getTitle();
    Window getWindow(int height, Window parent, Player player, EctoToken plugin);
}
