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

public class SolariCrypt implements CryptAbility {

    @Override
    public void applyPassive(Player player) {
        long time = player.getWorld().getTime();
        boolean isDay = time < 12300 || time > 23850;

        if (isDay) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 40, 0, true, false));
        }
    }

    @Override
    public void useAbility1(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "solari_ability1")) {
            player.sendMessage("§cSolar Flare is on cooldown for " + manager.getRemainingCooldown(player, "solari_ability1") + "s");
            return;
        }

        Location loc = player.getLocation();
        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity target = (LivingEntity) entity;
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                target.setFireTicks(100);
            }
        }

        player.sendMessage("§6Solar Flare activated!");
        manager.setCooldown(player, "solari_ability1", 30);
    }

    @Override
    public void useAbility2(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "solari_ability2")) {
            player.sendMessage("§cSunbeam is on cooldown for " + manager.getRemainingCooldown(player, "solari_ability2") + "s");
            return;
        }

        LivingEntity target = player.getTargetEntity(15, false) instanceof LivingEntity ?
                (LivingEntity) player.getTargetEntity(15, false) : null;

        if (target != null && target != player) {
            target.damage(6.0, player);
            target.setFireTicks(100);
            player.sendMessage("§6Sunbeam hit " + target.getName() + "!");
        } else {
            player.sendMessage("§cNo target found!");
        }

        manager.setCooldown(player, "solari_ability2", 20);
    }

    @Override
    public String getAbility1Name() {
        return "Solar Flare";
    }

    @Override
    public String getAbility2Name() {
        return "Sunbeam";
    }
}