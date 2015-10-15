package com.demonwav.ectotokens.util;

import org.bukkit.Bukkit;

import java.nio.ByteBuffer;
import java.util.UUID;

public class Util {

    public static byte[] UUIDToByte(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    public static UUID byteToUUID(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        return new UUID(buffer.getLong(), buffer.getLong());
    }

    public static void runTask(Runnable runnable) {
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("EctoTokens"), runnable);
    }

    public static void runTaskAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("EctoTokens"), runnable);
    }
}
