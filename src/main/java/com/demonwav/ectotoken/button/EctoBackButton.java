package com.demonwav.ectotoken.button;

import java.util.Collections;
import java.util.List;

public class EctoBackButton extends EctoButton {

    public EctoBackButton(String title, List<String> desc, int button, short data) {
        super(title, desc, button, data, null, 0, Collections.singletonList((window, player, plugin) -> {
            if (window.hasParent()) {
                window.getParent().createWindow();
            }
        }));
    }
}
