package com.demonwav.ectotoken.config.shop.actions;

import com.demonwav.ectotoken.action.ApplyEffectsAction;
import com.demonwav.ectotoken.config.ActionConfig;
import com.demonwav.ectotoken.config.Config;
import com.demonwav.ectotoken.config.Configs;

import lombok.Data;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.logging.Logger;

@Data
public class ApplyEffectsActionConfig implements ActionConfig {

    private List<Effect> effects;

    @Override
    public boolean validate(Logger logger) {
        Configs.removeNulls(effects);
        if (effects == null || effects.isEmpty()) {
            logger.warning("Apply Effects action defined with no effects.");
        }
        return effects == null || Configs.validate(logger, effects);
    }

    @Override
    public ApplyEffectsAction getAction() {
        return new ApplyEffectsAction(this);
    }

    @Data
    public static class Effect implements Config {

        private int duration;
        private int amplifier;
        private Boolean ambient;
        private Boolean particles;
        private String effect;

        @Override
        public boolean validate(Logger logger) {

            ambient = ambient == null ? false : ambient;
            particles = particles == null ? false : particles;

            boolean result = true;

            if (duration < 1) {
                logger.severe("Effect duration must be greater than 0!");
                result = false;
            }
            if (amplifier < 1) {
                logger.severe("Effect amplifier must be greater than 0!");
                result = false;
            }
            if (effect == null || effect.trim().isEmpty()) {
                logger.severe("Potion effect type must be set!");
                result = false;
            } else if (PotionEffectType.getByName(effect) == null) {
                logger.severe( effect + " is not a valid potion effect type!");
                result = false;
            }
            return result;
        }
    }
}
