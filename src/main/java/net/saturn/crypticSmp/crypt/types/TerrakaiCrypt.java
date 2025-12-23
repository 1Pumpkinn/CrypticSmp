package net.saturn.crypticSmp.crypt.types;

import net.saturn.crypticSmp.crypt.CryptAbility;
import net.saturn.crypticSmp.crypt.CryptManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class TerrakaiCrypt implements CryptAbility {

    @Override
    public void applyPassive(Player player) {
        Location loc = player.getLocation();
        boolean nearStone = false;

        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    Block block = loc.clone().add(x, y, z).getBlock();
                    Material type = block.getType();
                    if (type.name().contains("STONE") || type.name().contains("DIRT") ||
                            type.name().contains("GRASS") || type == Material.DEEPSLATE) {
                        nearStone = true;
                        break;
                    }
                }
            }
        }

        if (nearStone) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 40, 0, true, false));
        }
    }

    @Override
    public void useAbility1(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "terrakai_ability1")) {
            player.sendMessage("§cStone Skin is on cooldown for " + manager.getRemainingCooldown(player, "terrakai_ability1") + "s");
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 0));
        player.sendMessage("§7Stone Skin activated!");

        manager.setCooldown(player, "terrakai_ability1", 35);
    }

    @Override
    public void useAbility2(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "terrakai_ability2")) {
            player.sendMessage("§cTerra Slam is on cooldown for " + manager.getRemainingCooldown(player, "terrakai_ability2") + "s");
            return;
        }

        // Launch player upward
        player.setVelocity(new Vector(0, 1.5, 0));

        // Schedule the slam after a delay
        player.getServer().getScheduler().runTaskLater(
                player.getServer().getPluginManager().getPlugin("CrypticSmp"),
                () -> {
                    if (player.isOnGround()) {
                        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
                            if (entity instanceof LivingEntity && entity != player) {
                                Vector knockback = entity.getLocation().toVector()
                                        .subtract(player.getLocation().toVector())
                                        .normalize()
                                        .multiply(2)
                                        .setY(0.8);
                                entity.setVelocity(knockback);
                            }
                        }
                        player.sendMessage("§7Terra Slam!");
                    }
                }, 20L
        );

        manager.setCooldown(player, "terrakai_ability2", 40);
    }

    @Override
    public String getAbility1Name() {
        return "Stone Skin";
    }

    @Override
    public String getAbility2Name() {
        return "Terra Slam";
    }
}