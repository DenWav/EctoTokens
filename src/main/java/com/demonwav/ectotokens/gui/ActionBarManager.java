package com.demonwav.ectotokens.gui;

import com.demonwav.ectotokens.EctoTokens;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ActionBarManager {
    @Getter
    private static ActionBarManager instance;
    private HashMap<Player, String> messages = new HashMap<>();
    private HashMap<Player, Integer> altTime = new HashMap<>();
    private HashMap<Player, String> altString = new HashMap<>();

    public ActionBarManager(EctoTokens plugin) {
        instance = this;
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                // Remove null values
                Iterator<Map.Entry<Player, String>> iterator = messages.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Player, String> entry = iterator.next();
                    if (entry.getValue() == null)
                        iterator.remove();
                }
                // Send the action bar to the non-null parts
                for (Map.Entry<Player, String> entry : messages.entrySet()) {
                    setActionBar(entry.getKey(), entry.getValue());
                }
            }
        }, 1, 10);
    }

    public void setActionBar(Player player, String message) {
        if (altTime.containsKey(player)) {
            Integer tick = altTime.get(player);
            tick--;
            if (tick <= 0 || !altString.containsKey(player)) {
                altTime.remove(player);
                altString.remove(player);
            } else {
                altTime.put(player, tick);
                message = altString.get(player);
            }
        }
        CraftPlayer p = (CraftPlayer) player;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        p.getHandle().playerConnection.sendPacket(ppoc);
    }

    public void setActionBarMomentarily(Player player, String message, int ticks) {
        altString.put(player, message);
        altTime.put(player, ticks);
        setActionBar(player, message);
    }

    public void persistMessage(Player player, String message) {
        messages.put(player, message);
        altTime.remove(player);
        altString.remove(player);
    }

    public void stopPersist(Player player) {
        messages.remove(player);
        altTime.remove(player);
        altString.remove(player);
    }
}
