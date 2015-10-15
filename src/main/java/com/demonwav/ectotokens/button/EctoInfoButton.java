package com.demonwav.ectotokens.button;

import com.demonwav.ectotokens.action.Action;

import java.util.Collections;
import java.util.List;

public class EctoInfoButton extends EctoButton {

    public EctoInfoButton(String title, List<String> desc, int button, short data) {
        super(title, desc, button, data, null, 0, Collections.<Action>emptyList());
    }
}
