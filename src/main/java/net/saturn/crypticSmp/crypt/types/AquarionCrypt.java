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

public class AquarionCrypt implements CryptAbility {

    @Override
    public void applyPassive(Player player) {
        if (player.isInWater()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 40, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 40, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 40, 0, true, false));
        }
    }

    @Override
    public void useAbility1(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "aquarion_ability1")) {
            player.sendMessage("§cTidal Wave is on cooldown for " + manager.getRemainingCooldown(player, "aquarion_ability1") + "s");
            return;
        }

        Vector direction = player.getLocation().getDirection().normalize();
        Location playerLoc = player.getLocation();

        for (Entity entity : player.getNearbyEntities(6, 6, 6)) {
            if (entity instanceof LivingEntity && entity != player) {
                Location entityLoc = entity.getLocation();
                Vector toEntity = entityLoc.toVector().subtract(playerLoc.toVector()).normalize();

                // Check if entity is in front of player (cone shape)
                if (direction.dot(toEntity) > 0.5) {
                    Vector pushBack = direction.multiply(2).setY(0.5);
                    entity.setVelocity(pushBack);
                }
            }
        }

        player.sendMessage("§bTidal Wave!");
        manager.setCooldown(player, "aquarion_ability1", 30);
    }

    @Override
    public void useAbility2(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "aquarion_ability2")) {
            player.sendMessage("§cOcean's Grasp is on cooldown for " + manager.getRemainingCooldown(player, "aquarion_ability2") + "s");
            return;
        }

        LivingEntity target = player.getTargetEntity(10, false) instanceof LivingEntity ?
                (LivingEntity) player.getTargetEntity(10, false) : null;

        if (target != null && target != player) {
            Vector pull = player.getLocation().toVector()
                    .subtract(target.getLocation().toVector())
                    .normalize()
                    .multiply(1.5)
                    .setY(0.5);
            target.setVelocity(pull);
            player.sendMessage("§bOcean's Grasp pulled " + target.getName() + "!");
        } else {
            player.sendMessage("§cNo target found!");
        }

        manager.setCooldown(player, "aquarion_ability2", 20);
    }

    @Override
    public String getAbility1Name() {
        return "Tidal Wave";
    }

    @Override
    public String getAbility2Name() {
        return "Ocean's Grasp";
    }
}