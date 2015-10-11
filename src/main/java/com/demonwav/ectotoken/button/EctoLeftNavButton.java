package com.demonwav.ectotoken.button;

import java.util.Collections;
import java.util.List;

public class EctoLeftNavButton extends EctoButton {

    public EctoLeftNavButton(String title, List<String> desc, int button, short data) {
        super(title, desc, button, data, null, 0, Collections.singletonList((window, player, plugin) -> {
            if (window.hasPreviousPage()) {
                window.previousPage();
            }
        }));
    }
}
