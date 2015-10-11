package com.demonwav.ectotoken.config.main.nav;

import com.demonwav.ectotoken.button.Button;
import com.demonwav.ectotoken.button.EctoCloseButton;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EctoCloseButtonConfig extends NavButtonConfig {

    @Override
    public Button getButton() {
        return new EctoCloseButton(getTitle(), getDesc(), getItemId(), getItemData());
    }
}
