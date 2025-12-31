package me.Proba.crypt.crypts.crypts;

import me.Proba.crypt.CryptJavaPlugin;
import me.Proba.crypt.crypts.utils.CooldownUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Umbrae {

    private final CryptJavaPlugin plugin;

    public Umbrae(CryptJavaPlugin plugin) { this.plugin = plugin; }

    public void enable() {
        Bukkit.getScheduler().runTaskTimer(plugin.getPlugin(CryptJavaPlugin.class), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isSneaking() && p.getLocation().getBlock().getLightLevel() < 7) {
                    p.setInvisible(true);
                } else {
                    p.setInvisible(false);
                }
            }
        }, 0L, 20L);
    }

    public void shadowClone(Player p) {
        if (CooldownUtil.isOnCooldown(p, "umbrae_clone")) return;
        CooldownUtil.setCooldown(p, "umbrae_clone", 45_000);
        // TODO: spawn static clone at player location
    }

    public void fadeStep(Player p) {
        if (CooldownUtil.isOnCooldown(p, "umbrae_fade")) return;
        CooldownUtil.setCooldown(p, "umbrae_fade", 25_000);
        // TODO: teleport 8 blocks in facing direction
    }
}
