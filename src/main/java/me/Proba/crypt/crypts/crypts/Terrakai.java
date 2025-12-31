package me.Proba.crypt.crypts.crypts;

import me.Proba.crypt.CryptJavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Terrakai {

    private final CryptJavaPlugin plugin;

    public Terrakai(CryptJavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        Bukkit.getScheduler().runTaskTimer(plugin.getPlugin(CryptJavaPlugin.class), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 0));
                p.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 40, 0));

            }
        }, 0L, 20L);
    }

    public void stoneSkin(Player p) { /* TODO */ }

    public void terraSlam(Player p) { /* TODO */ }
}
