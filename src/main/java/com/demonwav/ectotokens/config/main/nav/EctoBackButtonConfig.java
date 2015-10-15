package com.demonwav.ectotokens.config.main.nav;

import com.demonwav.ectotokens.button.Button;
import com.demonwav.ectotokens.button.EctoBackButton;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EctoBackButtonConfig extends NavButtonConfig {

    @Override
    public Button getButton() {
        return new EctoBackButton(getTitle(), getDesc(), getItemId(), getItemData());
    }
}
