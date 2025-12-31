package me.Proba.crypt.crypts.crypts;

import me.Proba.crypt.CryptJavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Nytheris {

    private final CryptJavaPlugin plugin;

    public Nytheris(CryptJavaPlugin plugin) { this.plugin = plugin; }

    public void enable() {
        Bukkit.getScheduler().runTaskTimer(plugin.getPlugin(CryptJavaPlugin.class), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getWorld().getEnvironment().name().equals("THE_NETHER")) {
                    // TODO: Fire/Lava immunity + Strength I
                }
            }
        }, 0L, 20L);
    }

    public void lavaSurge(Player p) { /* TODO */ }

    public void infernalPulse(Player p) { /* TODO */ }
}
