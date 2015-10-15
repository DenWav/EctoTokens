package com.demonwav.ectotokens.button;

import com.demonwav.ectotokens.EctoTokens;
import com.demonwav.ectotokens.action.Action;
import com.demonwav.ectotokens.gui.Window;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class EctoLeftNavButton extends EctoButton {

    public EctoLeftNavButton(String title, List<String> desc, int button, short data) {
        super(title, desc, button, data, null, 0, Collections.<Action>singletonList(new Action() {
            @Override
            public void run(Window window, Player player, EctoTokens plugin) {
                if (window.hasPreviousPage()) {
                    window.previousPage();
                }
            }
        }));
    }
}
