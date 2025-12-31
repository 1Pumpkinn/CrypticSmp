package me.Proba.crypt.crypts.crypts;

import me.Proba.crypt.CryptJavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Aquarion {

    private final CryptJavaPlugin plugin;

    public Aquarion(CryptJavaPlugin plugin) { this.plugin = plugin; }

    public void enable() {
        Bukkit.getScheduler().runTaskTimer(plugin.getPlugin(CryptJavaPlugin.class), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getLocation().getBlock().isLiquid()) {
                    // TODO: Water breathing + Dolphin's grace + conduit power
                }
            }
        }, 0L, 20L);
    }

    public void tidalWave(Player p) { /* TODO */ }

    public void oceansGrasp(Player p) { /* TODO */ }
}
