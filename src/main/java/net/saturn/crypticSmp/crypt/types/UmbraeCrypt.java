package net.saturn.crypticSmp.crypt.types;

import net.saturn.crypticSmp.CrypticSmp;
import net.saturn.crypticSmp.crypt.CryptAbility;
import net.saturn.crypticSmp.crypt.CryptManager;
import org.bukkit.Location;
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

            // Store original location for camera locking
            Location originalLoc = target.getLocation().clone();

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