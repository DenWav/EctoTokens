package com.demonwav.ectotoken.config.shop.actions;

import com.demonwav.ectotoken.action.GiveItemsAction;
import com.demonwav.ectotoken.config.ActionConfig;
import com.demonwav.ectotoken.config.Config;
import com.demonwav.ectotoken.util.StringUtil;

import lombok.Data;
import org.bukkit.enchantments.Enchantment;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Data
public class GiveItemsActionConfig implements ActionConfig {

    private List<Item> items;


    @Override
    public boolean validate(Logger logger) {
        items = Config.removeNulls(items);
        if (items == null || items.isEmpty()) {
            logger.warning("Give Item action defined with no items.");
        }
        return items == null || Config.validate(logger, items);
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

            lore = Config.removeNulls(lore);
            enchants = Config.removeNulls(enchants);

            displayName = StringUtil.color(displayName);
            lore = lore.stream().filter(s -> !s.trim().isEmpty()).map(StringUtil::color).collect(Collectors.toList());

            if (quantity < 1) {
                logger.severe("Quantity for give items must be 1 or more!");
                result = false;
            }
            if (enchants != null) {
                System.out.println(enchants.toString());
                result &= Config.validate(logger, enchants);
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
