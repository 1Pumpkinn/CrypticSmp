package net.saturn.crypticSmp.crypt.types;

import net.saturn.crypticSmp.crypt.CryptAbility;
import net.saturn.crypticSmp.crypt.CryptManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class ZepharaCrypt implements CryptAbility {

    @Override
    public void applyPassive(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 40, 0, true, false));
    }

    @Override
    public void useAbility1(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "zephara_ability1")) {
            player.sendMessage("§cGale Dash is on cooldown for " + manager.getRemainingCooldown(player, "zephara_ability1") + "s");
            return;
        }

        Vector direction = player.getLocation().getDirection().normalize().multiply(2);
        Location current = player.getLocation();

        // Dash forward and damage entities in path
        for (int i = 0; i < 5; i++) {
            current.add(direction);
            for (Entity entity : current.getWorld().getNearbyEntities(current, 1.5, 1.5, 1.5)) {
                if (entity instanceof LivingEntity && entity != player) {
                    ((LivingEntity) entity).damage(4.0, player);
                }
            }
        }

        player.setVelocity(player.getLocation().getDirection().normalize().multiply(2.5));
        player.sendMessage("§fGale Dash!");

        manager.setCooldown(player, "zephara_ability1", 20);
    }

    @Override
    public void useAbility2(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "zephara_ability2")) {
            player.sendMessage("§cWindrise is on cooldown for " + manager.getRemainingCooldown(player, "zephara_ability2") + "s");
            return;
        }

        player.setVelocity(new Vector(0, 2.0, 0));
        player.sendMessage("§fWindrise!");

        manager.setCooldown(player, "zephara_ability2", 30);
    }

    @Override
    public String getAbility1Name() {
        return "Gale Dash";
    }

    @Override
    public String getAbility2Name() {
        return "Windrise";
    }
}