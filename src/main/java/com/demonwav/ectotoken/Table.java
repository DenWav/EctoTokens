package com.demonwav.ectotoken;

import java.util.Arrays;
import java.util.List;

public enum Table {

    BALANCE("CREATE TABLE `balance` (" +
        "`id` INT(10) UNSIGNED NOT NULL," +
        "`amount` BIGINT(20) NOT NULL," +
        "UNIQUE KEY `unique_id` (`id`))" +
        "ENGINE = InnoDB DEFAULT CHARSET = utf8",
        Arrays.asList("id", "amount")),
    PLAYERS("CREATE TABLE `players` (" +
        "`uuid` BINARY(16) NOT NULL," +
        "`name` VARCHAR(16) NOT NULL," +
        "`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT," +
        "PRIMARY KEY (`uuid`)," +
        "UNIQUE KEY `id` (`id`))" +
        "ENGINE = InnoDB DEFAULT CHARSET = utf8",
        Arrays.asList("uuid", "name", "id")),
    TRANSACTIONS("CREATE TABLE `transactions` (" +
        "`id` INT(10) UNSIGNED NOT NULL," +
        "`datetime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
        "`description` VARCHAR(200) NOT NULL," +
        "`amount` BIGINT(20) NOT NULL," +
        "UNIQUE KEY `id` (`id`, `datetime`, `description`, `amount`))" +
        "ENGINE = InnoDB DEFAULT CHARSET = utf8",
        Arrays.asList("id", "datetime", "description", "amount")),
    COUPON("CREATE TABLE `coupon` (" +
        "`coupon_id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT," +
        "`coupon_name` VARCHAR(25) NOT NULL," +
        "`amount` BIGINT(20) NOT NULL," +
        "`uses` INT(11) NOT NULL," +
        "PRIMARY KEY (`coupon_id`)," +
        "UNIQUE KEY `unique_coupon_name` (`coupon_name`))" +
        "ENGINE = InnoDB DEFAULT CHARSET = utf8",
        Arrays.asList("coupon_id", "coupon_name", "amount", "uses")),
    COUPON_USE("CREATE TABLE `coupon_use` (" +
        "`coupon_id` INT(10) UNSIGNED NOT NULL," +
        "`player_id` INT(10) UNSIGNED NOT NULL," +
        "`datetime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
        "UNIQUE KEY `use` (`coupon_id`, `player_id`))" +
        "ENGINE = InnoDB DEFAULT CHARSET = utf8",
        Arrays.asList("coupon_id", "player_id", "datetime")),
    LOTTERY("CREATE TABLE `lottery` (" +
        "`lottery_id` VARCHAR(50) NOT NULL," +
        "`amount` BIGINT(20) NOT NULL," +
        "PRIMARY KEY (`lottery_id`)," +
        "UNIQUE KEY `unique_lottery_id` (`lottery_id`))" +
        "ENGINE = InnoDB DEFAULT CHARSET = utf8",
        Arrays.asList("lottery_id", "amount"));

    private final String create;
    private final List<String> columnNames;

    Table(String create, List<String> columnNames) {
        this.create = create;
        this.columnNames = columnNames;
    }

    public final String getCreate() {
        return create;
    }

    public final String getName() {
        return name().toLowerCase();
    }

    public final int getColumns() {
        return columnNames.size();
    }

    public final List<String> getColumnNames() {
        return columnNames;
    }

}
