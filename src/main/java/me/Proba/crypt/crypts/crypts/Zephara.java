package me.Proba.crypt.crypts.crypts;

import me.Proba.crypt.CryptJavaPlugin;
import me.Proba.crypt.crypts.utils.CooldownUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Zephara implements Listener {

    private final CryptJavaPlugin plugin;

    public Zephara(CryptJavaPlugin plugin) {
        this.plugin = plugin;
        // Register events to handle fall damage
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /* =========================
       Passive: Wind Blessing
       Speed + Haste while equipped
       ========================= */
    public void enable() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {

                // Check if the player has Zephara anywhere in their inventory
                if (hasZepharaCrypt(p)) {
                    p.addPotionEffect(new PotionEffect(
                            PotionEffectType.SPEED, 40, 0, true, false
                    ));
                    p.addPotionEffect(new PotionEffect(
                            PotionEffectType.HASTE, 40, 0, true, false
                    ));
                }
            }
        }, 0L, 20L);
    }

    /* =========================
       Ability 1: Gale Dash
       ========================= */
    public void galeDash(Player p) {
        if (CooldownUtil.isOnCooldown(p, "zephara_dash")) {
            long remaining = CooldownUtil.getRemaining(p, "zephara_dash");
            p.sendMessage(ChatColor.RED + "Gale Dash is on cooldown (" + (remaining / 1000) + "s).");
            return;
        }

        CooldownUtil.setCooldown(p, "zephara_dash", 10_000);

        Vector direction = p.getLocation().getDirection().normalize().multiply(2.2);
        direction.setY(1);
        p.setVelocity(direction);
    }

    /* =========================
       Ability 2: Wind Rise
       ========================= */
    public void windRise(Player p) {
        if (CooldownUtil.isOnCooldown(p, "zephara_rise")) {
            long remaining = CooldownUtil.getRemaining(p, "zephara_rise");
            p.sendMessage(ChatColor.RED + "Wind Rise is on cooldown (" + (remaining / 1000) + "s).");
            return;
        }

        CooldownUtil.setCooldown(p, "zephara_rise", 20_000);

        p.setVelocity(new Vector(0, 2.2, 0));
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW_FALLING, 20 * 7, 0
        ));
    }

    /* =========================
       Event: No fall damage if player has Zephara crypt anywhere
       ========================= */
    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL &&
                hasZepharaCrypt(player)) {
            event.setCancelled(true);
           // player.sendMessage(ChatColor.AQUA + "Zephara protects you from fall damage!");
        }
    }

    /**
     * Checks if the player has a Zephara Crypt anywhere in their inventory.
     */
    private boolean hasZepharaCrypt(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || !item.hasItemMeta()) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName() &&
                    ChatColor.stripColor(meta.getDisplayName()).equalsIgnoreCase("Zephara Crypt")) {
                return true;
            }
        }
        return false;
    }
}
