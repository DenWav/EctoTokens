package com.demonwav.ectotoken;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Transaction {
    public enum Time { BEFORE, AFTER }
    public enum Amount { MORE, LESS }

    private OfflinePlayer player;
    private Timestamp timestamp;
    private String description;
    private long amount;

}
