package me.Proba.crypt.crypts.crypts;

import me.Proba.crypt.CryptJavaPlugin;
import me.Proba.crypt.crypts.utils.CooldownUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Random;

public class Thundros implements Listener {

    private final CryptJavaPlugin plugin;
    private final Random random = new Random();

    public Thundros(CryptJavaPlugin plugin) {
        this.plugin = plugin;
    }

    /* =========================
       Passive: 20% lightning zap
       ========================= */
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;

        if (random.nextDouble() <= 0.20) {
            victim.getWorld().strikeLightningEffect(victim.getLocation());
            victim.damage(4.0, attacker);
        }
    }

    /* =========================
       Ability 1: Lightning Call
       ========================= */
    public void lightningCall(Player p) {

        if (CooldownUtil.isOnCooldown(p, "thundros_lightning")) {
            long remaining = CooldownUtil.getRemaining(p, "thundros_lightning");
            p.sendMessage(ChatColor.RED + "Lightning Call is on cooldown (" +
                    (remaining / 1000) + "s).");
            return;
        }

        CooldownUtil.setCooldown(p, "thundros_lightning", 25_000);

        Player target = null;
        double range = 10;

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(p)) continue;
            if (other.getWorld() != p.getWorld()) continue;
            if (other.getLocation().distance(p.getLocation()) <= range) {
                target = other;
                break;
            }
        }

        if (target != null) {
            target.getWorld().strikeLightning(target.getLocation());
        } else {
            p.sendMessage(ChatColor.GRAY + "No target in range.");
        }
    }

    /* =========================
       Ability 2: Storm Burst
       ========================= */
    public void stormBurst(Player p) {

        if (CooldownUtil.isOnCooldown(p, "thundros_storm")) {
            long remaining = CooldownUtil.getRemaining(p, "thundros_storm");
            p.sendMessage(ChatColor.RED + "Storm Burst is on cooldown (" +
                    (remaining / 1000) + "s).");
            return;
        }

        CooldownUtil.setCooldown(p, "thundros_storm", 60_000);

        Location center = p.getLocation();

        p.getWorld().setStorm(true);
        p.getWorld().setWeatherDuration(20 * 10); // 10 seconds

        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            for (int i = 0; i < 3; i++) {
                Location strike = center.clone().add(
                        random.nextInt(10) - 5,
                        0,
                        random.nextInt(10) - 5
                );
                strike.getWorld().strikeLightning(strike);
            }
        }, 0L, 20L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            p.getWorld().setStorm(false);
        }, 20L * 10);
    }
}
