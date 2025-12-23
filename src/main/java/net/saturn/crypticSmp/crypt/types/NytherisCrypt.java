package net.saturn.crypticSmp.crypt.types;

import net.saturn.crypticSmp.crypt.CryptAbility;
import net.saturn.crypticSmp.crypt.CryptManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NytherisCrypt implements CryptAbility {

    @Override
    public void applyPassive(Player player) {
        player.setFireTicks(0); // Fire immunity

        if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 40, 0, true, false));
        }
    }

    @Override
    public void useAbility1(Player player, net.saturn.crypticSmp.crypt.CryptManager manager) {
        if (manager.isOnCooldown(player, "nytheris_ability1")) {
            player.sendMessage("§cLava Surge is on cooldown for " + manager.getRemainingCooldown(player, "nytheris_ability1") + "s");
            return;
        }

        Location loc = player.getLocation().subtract(0, 1, 0);

        // Create a small lava pool (3x3)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location lavaLoc = loc.clone().add(x, 0, z);
                if (lavaLoc.getBlock().getType() == Material.AIR ||
                        lavaLoc.getBlock().getType().name().contains("GRASS")) {
                    lavaLoc.getBlock().setType(Material.LAVA);

                    // Remove lava after 10 seconds
                    player.getServer().getScheduler().runTaskLater(
                            player.getServer().getPluginManager().getPlugin("CrypticSmp"),
                            () -> {
                                if (lavaLoc.getBlock().getType() == Material.LAVA) {
                                    lavaLoc.getBlock().setType(Material.AIR);
                                }
                            }, 200L
                    );
                }
            }
        }

        player.sendMessage("§6Lava Surge!");
        manager.setCooldown(player, "nytheris_ability1", 45);
    }

    @Override
    public void useAbility2(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "nytheris_ability2")) {
            player.sendMessage("§cInfernal Pulse is on cooldown for " + manager.getRemainingCooldown(player, "nytheris_ability2") + "s");
            return;
        }

        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity target = (LivingEntity) entity;
                target.damage(6.0, player);
                target.setFireTicks(100);
            }
        }

        player.sendMessage("§6Infernal Pulse!");
        manager.setCooldown(player, "nytheris_ability2", 35);
    }

    @Override
    public String getAbility1Name() {
        return "Lava Surge";
    }

    @Override
    public String getAbility2Name() {
        return "Infernal Pulse";
    }
}