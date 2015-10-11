package com.demonwav.ectotoken.button;

import java.util.Collections;
import java.util.List;

public class EctoCloseButton extends EctoButton {

    public EctoCloseButton(String title, List<String> desc, int button, short data) {
        super(title, desc, button, data, null, 0, Collections.singletonList((window, player, plugin) -> {
            player.closeInventory();
        }));
    }
}
