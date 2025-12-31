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
            player.sendMessage("§cOcean's Slam is on cooldown for " + manager.getRemainingCooldown(player, "aquarion_ability2") + "s");
            return;
        }

        LivingEntity target = player.getTargetEntity(10, false) instanceof LivingEntity ?
                (LivingEntity) player.getTargetEntity(10, false) : null;

        if (target != null && target != player) {
            player.sendMessage("§bOcean's Slam hit " + target.getName() + "!");

            // Launch target upward
            target.setVelocity(new Vector(0, 1.5, 0));

            // Spawn water particles during launch
            new BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    if (ticks >= 20 || !target.isValid()) {
                        cancel();
                        return;
                    }

                    Location targetLoc = target.getLocation().add(0, 1, 0);
                    target.getWorld().spawnParticle(
                            Particle.BUBBLE_COLUMN_UP,
                            targetLoc,
                            10, 0.3, 0.5, 0.3, 0.1
                    );
                    target.getWorld().spawnParticle(
                            Particle.SPLASH,
                            targetLoc,
                            5, 0.2, 0.2, 0.2, 0.1
                    );

                    ticks++;
                }
            }.runTaskTimer(CrypticSmp.getInstance(), 0L, 1L);

            // Slam down after 1 second (20 ticks) and deal true damage
            CrypticSmp.getInstance().getServer().getScheduler().runTaskLater(
                    CrypticSmp.getInstance(),
                    () -> {
                        if (target.isValid()) {
                            // Force downward velocity
                            target.setVelocity(new Vector(0, -2.0, 0));

                            // Deal 3 hearts (6.0 HP) of true damage after a brief moment
                            CrypticSmp.getInstance().getServer().getScheduler().runTaskLater(
                                    CrypticSmp.getInstance(),
                                    () -> {
                                        if (target.isValid()) {
                                            // Apply true damage by setting health directly
                                            double newHealth = target.getHealth() - 6.0;
                                            if (newHealth < 0) newHealth = 0;
                                            target.setHealth(newHealth);

                                            // Spawn impact particles
                                            Location impactLoc = target.getLocation();

                                            // Water splash particles
                                            target.getWorld().spawnParticle(
                                                    Particle.SPLASH,
                                                    impactLoc.clone().add(0, 0.5, 0),
                                                    50, 1, 0.5, 1, 0.2
                                            );
                                            target.getWorld().spawnParticle(
                                                    Particle.BUBBLE,
                                                    impactLoc.clone().add(0, 0.5, 0),
                                                    30, 1, 0.5, 1, 0.1
                                            );

                                            // Ground impact particles (block crack effect)
                                            target.getWorld().spawnParticle(
                                                    Particle.BLOCK,
                                                    impactLoc.clone().add(0, 0.1, 0),
                                                    80, 1.5, 0.1, 1.5, 0.1,
                                                    impactLoc.clone().subtract(0, 1, 0).getBlock().getBlockData()
                                            );

                                            // Additional ground debris effect
                                            target.getWorld().spawnParticle(
                                                    Particle.BLOCK_CRUMBLE,
                                                    impactLoc.clone().add(0, 0.1, 0),
                                                    60, 1.2, 0.1, 1.2, 0.1,
                                                    impactLoc.clone().subtract(0, 1, 0).getBlock().getBlockData()
                                            );

                                            // Explosion effect for impact
                                            target.getWorld().spawnParticle(
                                                    Particle.EXPLOSION,
                                                    impactLoc.clone().add(0, 0.2, 0),
                                                    3, 0.5, 0.1, 0.5, 0
                                            );
                                        }
                                    },
                                    5L
                            );
                        }
                    },
                    20L
            );

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
}