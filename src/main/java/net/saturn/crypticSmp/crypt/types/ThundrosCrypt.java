package net.saturn.crypticSmp.crypt.types;

import net.saturn.crypticSmp.crypt.CryptAbility;
import net.saturn.crypticSmp.crypt.CryptManager;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Random;

public class ThundrosCrypt implements CryptAbility {
    private final Random random = new Random();

    @Override
    public void applyPassive(Player player) {
        // Passive is handled in event listener (20% chance on hit)
    }

    public void handlePassiveStrike(EntityDamageByEntityEvent event, Player player) {
        if (random.nextInt(100) < 20) { // 20% chance
            event.getEntity().getWorld().strikeLightning(event.getEntity().getLocation());
        }
    }

    @Override
    public void useAbility1(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "thundros_ability1")) {
            player.sendMessage("§cLightning Call is on cooldown for " + manager.getRemainingCooldown(player, "thundros_ability1") + "s");
            return;
        }

        LivingEntity target = player.getTargetEntity(10, false) instanceof LivingEntity ?
                (LivingEntity) player.getTargetEntity(10, false) : null;

        if (target != null && target != player) {
            target.getWorld().strikeLightning(target.getLocation());
            player.sendMessage("§eLightning Call struck " + target.getName() + "!");
        } else {
            player.sendMessage("§cNo target found!");
        }

        manager.setCooldown(player, "thundros_ability1", 25);
    }

    @Override
    public void useAbility2(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "thundros_ability2")) {
            player.sendMessage("§cStorm Burst is on cooldown for " + manager.getRemainingCooldown(player, "thundros_ability2") + "s");
            return;
        }

        Location center = player.getLocation();
        player.getWorld().setStorm(true);
        player.getWorld().setThundering(true);

        // Strike lightning in multiple locations around the player
        player.getServer().getScheduler().runTaskLater(
                player.getServer().getPluginManager().getPlugin("CrypticSmp"),
                () -> {
                    for (int i = 0; i < 5; i++) {
                        double angle = random.nextDouble() * Math.PI * 2;
                        double distance = 5 + random.nextDouble() * 5;
                        double x = Math.cos(angle) * distance;
                        double z = Math.sin(angle) * distance;
                        Location strikeLoc = center.clone().add(x, 0, z);
                        player.getWorld().strikeLightning(strikeLoc);
                    }
                }, 20L
        );

        player.sendMessage("§eStorm Burst!");
        manager.setCooldown(player, "thundros_ability2", 60);
    }

    @Override
    public String getAbility1Name() {
        return "Lightning Call";
    }

    @Override
    public String getAbility2Name() {
        return "Storm Burst";
    }
}