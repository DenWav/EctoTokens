package com.demonwav.ectotoken.button;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.action.Action;
import com.demonwav.ectotoken.gui.Window;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class EctoBackButton extends EctoButton {

    public EctoBackButton(String title, List<String> desc, int button, short data) {
        super(title, desc, button, data, null, 0, Collections.<Action>singletonList(new Action() {
            @Override
            public void run(Window window, Player player, EctoToken plugin) {
                if (window.hasParent()) {
                    window.getParent().createWindow();
                }
            }
        }));
    }
}
