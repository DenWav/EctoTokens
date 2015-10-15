package com.demonwav.ectotokens.config.main.nav;

import com.demonwav.ectotokens.button.Button;
import com.demonwav.ectotokens.button.EctoInfoButton;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EctoInfoButtonConfig extends NavButtonConfig {

    private transient String actualTitle;
    private transient List<String> actualDesc;

    @Override
    public Button getButton() {
        return new EctoInfoButton(actualTitle, actualDesc, getItemId(), getItemData());
    }
}
