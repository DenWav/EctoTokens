package com.demonwav.ectotokens.config.main.nav;

import com.demonwav.ectotokens.button.Button;
import com.demonwav.ectotokens.button.EctoRightNavButton;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EctoRightNavButtonConfig extends NavButtonConfig {

    @Override
    public Button getButton() {
        return new EctoRightNavButton(getTitle(), getDesc(), getItemId(), getItemData());
    }
}
