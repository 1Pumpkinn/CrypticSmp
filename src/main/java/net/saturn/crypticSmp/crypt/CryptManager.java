package net.saturn.crypticSmp.crypt;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CryptManager {
    private final JavaPlugin plugin;
    private final Map<UUID, CryptType> playerCrypts = new HashMap<>();
    private final Map<String, Long> abilityCooldowns = new HashMap<>();

    public CryptManager(JavaPlugin plugin) {
        this.plugin = plugin;
        startPassiveEffectTask();
    }

    public void setCrypt(Player player, CryptType crypt) {
        playerCrypts.put(player.getUniqueId(), crypt);
        player.sendMessage("§6You have chosen the " + crypt.getDisplayName() + "§6!");
    }

    public CryptType getCrypt(Player player) {
        return playerCrypts.get(player.getUniqueId());
    }

    public boolean hasCrypt(Player player) {
        return playerCrypts.containsKey(player.getUniqueId());
    }

    public void removeCrypt(Player player) {
        playerCrypts.remove(player.getUniqueId());
    }

    public boolean isOnCooldown(Player player, String abilityKey) {
        String key = player.getUniqueId() + ":" + abilityKey;
        Long cooldownEnd = abilityCooldowns.get(key);
        if (cooldownEnd == null) return false;

        long currentTime = System.currentTimeMillis();
        if (currentTime >= cooldownEnd) {
            abilityCooldowns.remove(key);
            return false;
        }
        return true;
    }

    public void setCooldown(Player player, String abilityKey, int seconds) {
        String key = player.getUniqueId() + ":" + abilityKey;
        abilityCooldowns.put(key, System.currentTimeMillis() + (seconds * 1000L));
    }

    public long getRemainingCooldown(Player player, String abilityKey) {
        String key = player.getUniqueId() + ":" + abilityKey;
        Long cooldownEnd = abilityCooldowns.get(key);
        if (cooldownEnd == null) return 0;

        long remaining = (cooldownEnd - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    private void startPassiveEffectTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (!hasCrypt(player)) continue;

                    CryptType crypt = getCrypt(player);
                    if (crypt != null) {
                        crypt.applyPassiveEffects(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Run every second
    }

    public Map<UUID, CryptType> getAllPlayerCrypts() {
        return new HashMap<>(playerCrypts);
    }
}