package me.Proba.crypt.crypts.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CooldownUtil {

    private static final HashMap<UUID, HashMap<String, Long>> cooldowns = new HashMap<>();

    public static boolean isOnCooldown(Player player, String ability) {
        return cooldowns.containsKey(player.getUniqueId())
                && cooldowns.get(player.getUniqueId()).getOrDefault(ability, 0L) > System.currentTimeMillis();
    }

    public static void setCooldown(Player player, String ability, long millis) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(ability, System.currentTimeMillis() + millis);
    }

    public static long getRemaining(Player p, String solariFlare) {
        return 0;
    }
}
