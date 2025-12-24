package net.saturn.crypticSmp.crypt.types;

import net.saturn.crypticSmp.CrypticSmp;
import net.saturn.crypticSmp.crypt.CryptAbility;
import net.saturn.crypticSmp.crypt.CryptManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AquarionCrypt implements CryptAbility {
    private static final Set<UUID> pulledEntities = new HashSet<>();

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

                    // Spawn water splash particles
                    entity.getWorld().spawnParticle(
                            Particle.SPLASH,
                            entity.getLocation().add(0, 1, 0),
                            30, 0.5, 0.5, 0.5, 0.1
                    );
                }
            }
        }

        // Spawn wave particles
        Location particleLoc = playerLoc.clone();
        for (int i = 0; i < 6; i++) {
            final int distance = i;
            CrypticSmp.getInstance().getServer().getScheduler().runTaskLater(
                    CrypticSmp.getInstance(),
                    () -> {
                        Location waveLoc = particleLoc.clone().add(direction.clone().multiply(distance));
                        waveLoc.getWorld().spawnParticle(
                                Particle.BUBBLE,
                                waveLoc.add(0, 1, 0),
                                20, 1, 1, 1, 0.1
                        );
                        waveLoc.getWorld().spawnParticle(
                                Particle.SPLASH,
                                waveLoc,
                                15, 1, 0.5, 1, 0.1
                        );
                    },
                    i * 2L
            );
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
            // Mark entity as being pulled (to prevent movement but allow camera)
            pulledEntities.add(target.getUniqueId());

            // Apply slowness to prevent walking
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 10));
            target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 60, 128));

            player.sendMessage("§bOcean's Grasp pulled " + target.getName() + "!");

            // Pull the target over time (slower pull)
            new BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    if (ticks >= 60 || !target.isValid() || !player.isOnline()) {
                        pulledEntities.remove(target.getUniqueId());
                        cancel();
                        return;
                    }

                    // Calculate pull direction
                    Vector pull = player.getLocation().toVector()
                            .subtract(target.getLocation().toVector())
                            .normalize()
                            .multiply(0.3)
                            .setY(0.1);

                    // Apply pull velocity
                    target.setVelocity(pull);

                    // Spawn water particles
                    Location targetLoc = target.getLocation().add(0, 1, 0);
                    target.getWorld().spawnParticle(
                            Particle.BUBBLE,
                            targetLoc,
                            5, 0.3, 0.3, 0.3, 0.05
                    );
                    target.getWorld().spawnParticle(
                            Particle.DRIPPING_WATER,
                            targetLoc,
                            3, 0.3, 0.5, 0.3, 0
                    );

                    // Create water stream effect
                    Location playerLoc = player.getLocation().add(0, 1, 0);
                    Vector direction = targetLoc.toVector().subtract(playerLoc.toVector()).normalize();
                    for (double d = 0; d < playerLoc.distance(targetLoc); d += 0.5) {
                        Location particleLoc = playerLoc.clone().add(direction.clone().multiply(d));
                        particleLoc.getWorld().spawnParticle(
                                Particle.BUBBLE_COLUMN_UP,
                                particleLoc,
                                1, 0, 0, 0, 0
                        );
                    }

                    ticks += 2;
                }
            }.runTaskTimer(CrypticSmp.getInstance(), 0L, 2L);

        } else {
            player.sendMessage("§cNo target found!");
            return;
        }

        manager.setCooldown(player, "aquarion_ability2", 20);
    }

    @Override
    public String getAbility1Name() {
        return "Tidal Wave";
    }

    @Override
    public String getAbility2Name() {
        return "Ocean's Slam";
    }

    public static boolean isPulled(UUID entityId) {
        return pulledEntities.contains(entityId);
    }
}