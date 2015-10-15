package com.demonwav.ectotokens.config.shop.actions;

import com.demonwav.ectotokens.action.GiveItemsAction;
import com.demonwav.ectotokens.config.ActionConfig;
import com.demonwav.ectotokens.config.Config;
import com.demonwav.ectotokens.config.Configs;
import com.demonwav.ectotokens.util.StringUtil;

import lombok.Data;
import org.bukkit.enchantments.Enchantment;

import java.util.List;
import java.util.logging.Logger;

@Data
public class GiveItemsActionConfig implements ActionConfig {

    private List<Item> items;


    @Override
    public boolean validate(Logger logger) {
        Configs.removeNulls(items);
        if (items == null || items.isEmpty()) {
            logger.warning("Give Item action defined with no items.");
        }
        return items == null || Configs.validate(logger, items);
    }

    @Override
    public GiveItemsAction getAction() {
        return new GiveItemsAction(this);
    }

    @Data
    public static class Item implements Config {

        private int itemId;
        private short itemData;
        private int quantity;
        private String displayName;
        private List<String> lore;
        private List<Enchant> enchants;

        @Override
        public boolean validate(Logger logger) {
            boolean result = true;

            Configs.removeNulls(lore);
            Configs.removeNulls(enchants);

            displayName = StringUtil.color(displayName);
            Configs.removeEmptiesAndColor(lore);

            if (quantity < 1) {
                logger.severe("Quantity for give items must be 1 or more!");
                result = false;
            }
            if (enchants != null) {
                result &= Configs.validate(logger, enchants);
            }

            return result;
        }

        @Data
        public static class Enchant implements Config {
            private String enchantment;
            private int level;

            @Override
            public boolean validate(Logger logger) {
                boolean result = true;

                if (level < 1) {
                    logger.severe("Enchantment level must be greater than 0!");
                    result = false;
                }
                if (enchantment.trim().isEmpty()) {
                    logger.severe("Enchantment type must be set!");
                    result = false;
                } else if (Enchantment.getByName(enchantment) == null) {
                    logger.severe(enchantment + " is not a valid enchantment!");
                    result = false;
                }

                return result;
            }
        }
    }
}
