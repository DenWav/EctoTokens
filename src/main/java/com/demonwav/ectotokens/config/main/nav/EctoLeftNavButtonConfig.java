package com.demonwav.ectotokens.config.main.nav;

import com.demonwav.ectotokens.button.Button;
import com.demonwav.ectotokens.button.EctoLeftNavButton;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EctoLeftNavButtonConfig extends NavButtonConfig {

    @Override
    public Button getButton() {
        return new EctoLeftNavButton(getTitle(), getDesc(), getItemId(), getItemData());
    }
}
