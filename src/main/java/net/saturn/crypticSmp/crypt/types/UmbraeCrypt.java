package net.saturn.crypticSmp.crypt.types;

import net.saturn.crypticSmp.CrypticSmp;
import net.saturn.crypticSmp.crypt.CryptAbility;
import net.saturn.crypticSmp.crypt.CryptManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UmbraeCrypt implements CryptAbility {
    private static final Set<UUID> stunnedEntities = new HashSet<>();

    @Override
    public void applyPassive(Player player) {
        if (player.isSneaking() && player.getLocation().getBlock().getLightLevel() < 7) {
            player.addPotionEffect(
                    new PotionEffect(PotionEffectType.INVISIBILITY, 40, 0, true, false)
            );
        }
    }

    @Override
    public void useAbility1(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "umbrae_ability1")) {
            player.sendMessage("§cShadow Grasp is on cooldown for "
                    + manager.getRemainingCooldown(player, "umbrae_ability1") + "s");
            return;
        }

        LivingEntity target = player.getTargetEntity(10, false) instanceof LivingEntity ?
                (LivingEntity) player.getTargetEntity(10, false) : null;

        if (target != null && target != player) {
            // Add blindness
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));

            // Add slowness and jump boost negative to prevent movement
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 10));
            target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 100, 128));

            // Mark as stunned for camera lock
            stunnedEntities.add(target.getUniqueId());

            // Spawn shadow particles around target
            Location targetLoc = target.getLocation();
            CrypticSmp.getInstance().getServer().getScheduler().runTaskTimer(
                    CrypticSmp.getInstance(),
                    new Runnable() {
                        int ticks = 0;
                        @Override
                        public void run() {
                            if (ticks >= 100 || !target.isValid()) {
                                return;
                            }

                            // Spawn dark particles in a spiral around the target
                            Location loc = target.getLocation().add(0, 1, 0);
                            for (int i = 0; i < 3; i++) {
                                double angle = (ticks + i * 120) * 0.1;
                                double x = Math.cos(angle) * 1.5;
                                double z = Math.sin(angle) * 1.5;
                                loc.getWorld().spawnParticle(
                                        Particle.LARGE_SMOKE,
                                        loc.clone().add(x, 0, z),
                                        1, 0, 0, 0, 0
                                );
                                loc.getWorld().spawnParticle(
                                        org.bukkit.Particle.SQUID_INK,
                                        loc.clone().add(x, 0, z),
                                        2, 0.1, 0.1, 0.1, 0
                                );
                            }
                            ticks++;
                        }
                    },
                    0L, 2L
            );

            // Remove stun after 5 seconds
            CrypticSmp.getInstance().getServer().getScheduler().runTaskLater(
                    CrypticSmp.getInstance(),
                    () -> stunnedEntities.remove(target.getUniqueId()),
                    100L
            );

            player.sendMessage("§8Shadow Grasp captured " + target.getName() + "!");
        } else {
            player.sendMessage("§cNo target found!");
            return;
        }

        manager.setCooldown(player, "umbrae_ability1", 45);
    }

    @Override
    public void useAbility2(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "umbrae_ability2")) {
            player.sendMessage("§cFade Step is on cooldown for "
                    + manager.getRemainingCooldown(player, "umbrae_ability2") + "s");
            return;
        }

        Vector direction = player.getLocation().getDirection()
                .normalize()
                .multiply(15);

        Location newLoc = player.getLocation().add(direction);
        newLoc.setYaw(player.getLocation().getYaw());
        newLoc.setPitch(player.getLocation().getPitch());

        if (!newLoc.getBlock().isPassable()) {
            player.sendMessage("§cCannot teleport into solid blocks!");
            return;
        }

        player.teleport(newLoc);
        player.sendMessage("§8Fade Step!");

        manager.setCooldown(player, "umbrae_ability2", 30);
    }

    @Override
    public String getAbility1Name() {
        return "Shadow Grasp";
    }

    @Override
    public String getAbility2Name() {
        return "Fade Step";
    }

    public static boolean isStunned(UUID entityId) {
        return stunnedEntities.contains(entityId);
    }
}