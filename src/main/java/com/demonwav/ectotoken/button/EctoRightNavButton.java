package com.demonwav.ectotoken.button;

import java.util.Collections;
import java.util.List;

public class EctoRightNavButton extends EctoButton {

    public EctoRightNavButton(String title, List<String> desc, int button, short data) {
        super(title, desc, button, data, null, 0, Collections.singletonList((window, player, plugin) -> {
            if (window.hasNextPage()) {
                window.nextPage();
            }
        }));
    }
}
