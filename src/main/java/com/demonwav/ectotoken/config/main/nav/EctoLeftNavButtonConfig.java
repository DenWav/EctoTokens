package com.demonwav.ectotoken.config.main.nav;

import com.demonwav.ectotoken.button.Button;
import com.demonwav.ectotoken.button.EctoLeftNavButton;

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
