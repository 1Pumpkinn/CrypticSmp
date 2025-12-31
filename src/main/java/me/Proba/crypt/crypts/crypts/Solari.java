package me.Proba.crypt.crypts.crypts;

import me.Proba.crypt.CryptJavaPlugin;
import me.Proba.crypt.crypts.utils.CooldownUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Solari {

    private final CryptJavaPlugin plugin;

    public Solari(CryptJavaPlugin plugin) {
        this.plugin = plugin;
    }

    /* =========================
       Passive: Daytime buffs
       ========================= */
    public void enable() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getWorld().getTime() < 13000) {
                    p.addPotionEffect(new PotionEffect(
                            PotionEffectType.REGENERATION, 40, 0, true, false
                    ));
                    p.addPotionEffect(new PotionEffect(
                            PotionEffectType.FIRE_RESISTANCE, 40, 0, true, false
                    ));
                }
            }
        }, 0L, 20L);
    }

    /* =========================
       Ability 1: Solar Flare
       ========================= */
    public void solarFlare(Player p) {

        if (CooldownUtil.isOnCooldown(p, "solari_flare")) {
            long remaining = CooldownUtil.getRemaining(p, "solari_flare");
            p.sendMessage(ChatColor.RED + "Solar Flare is on cooldown (" +
                    (remaining / 1000) + "s).");
            return;
        }

        CooldownUtil.setCooldown(p, "solari_flare", 30_000);

        p.getNearbyEntities(5, 5, 5).forEach(entity -> {
            if (entity instanceof Player t && !t.equals(p)) {
                t.addPotionEffect(new PotionEffect(
                        PotionEffectType.BLINDNESS, 80, 0
                ));
                t.setFireTicks(60);
            }
        });
    }

    /* =========================
       Ability 2: Sunbeam
       ========================= */
    public void sunbeam(Player p) {

        if (CooldownUtil.isOnCooldown(p, "solari_sunbeam")) {
            long remaining = CooldownUtil.getRemaining(p, "solari_sunbeam");
            p.sendMessage(ChatColor.RED + "Sunbeam is on cooldown (" +
                    (remaining / 1000) + "s).");
            return;
        }

        CooldownUtil.setCooldown(p, "solari_sunbeam", 20_000);

        Location loc = p.getEyeLocation();
        for (int i = 0; i < 15; i++) {
            loc.add(loc.getDirection());
            p.getWorld().spawnParticle(org.bukkit.Particle.FLAME, loc, 1);

            p.getWorld().getNearbyEntities(loc, 1, 1, 1).forEach(entity -> {
                if (entity instanceof Player t && !t.equals(p)) {
                    t.setFireTicks(40);
                }
            });
        }
    }
}
