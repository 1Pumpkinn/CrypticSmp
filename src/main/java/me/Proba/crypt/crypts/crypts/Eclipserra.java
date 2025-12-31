package me.Proba.crypt.crypts.crypts;

import me.Proba.crypt.CryptJavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Eclipserra {

    private final CryptJavaPlugin plugin;

    public Eclipserra(CryptJavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        Bukkit.getScheduler().runTaskTimer(plugin.getPlugin(CryptJavaPlugin.class), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                long time = p.getWorld().getTime();
                if (time >= 13000 && time <= 23000) {
                    // Night: Strength + Speed
                } else {
                    // Day: Weakness + Slowness
                }
            }
        }, 0L, 20L);
    }

    public void lunarVeil(Player p) { /* TODO */ }

    public void moonStrike(Player p) { /* TODO */ }
}
