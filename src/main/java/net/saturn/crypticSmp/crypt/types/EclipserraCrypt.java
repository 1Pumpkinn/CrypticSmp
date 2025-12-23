package net.saturn.crypticSmp.crypt.types;

import net.saturn.crypticSmp.crypt.CryptAbility;
import net.saturn.crypticSmp.crypt.CryptManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EclipserraCrypt implements CryptAbility {

    @Override
    public void applyPassive(Player player) {
        long time = player.getWorld().getTime();
        boolean isNight = time >= 12300 && time <= 23850;

        if (isNight) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 40, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0, true, false));
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, true, false));
        }
    }

    @Override
    public void useAbility1(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "eclipserra_ability1")) {
            player.sendMessage("§cLunar Veil is on cooldown for " + manager.getRemainingCooldown(player, "eclipserra_ability1") + "s");
            return;
        }

        long time = player.getWorld().getTime();
        boolean isNight = time >= 12300 && time <= 23850;

        if (!isNight) {
            player.sendMessage("§cLunar Veil can only be used at night!");
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 160, 0));
        player.sendMessage("§5Lunar Veil activated!");

        manager.setCooldown(player, "eclipserra_ability1", 30);
    }

    @Override
    public void useAbility2(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "eclipserra_ability2")) {
            player.sendMessage("§cMoonstrike is on cooldown for " + manager.getRemainingCooldown(player, "eclipserra_ability2") + "s");
            return;
        }

        LivingEntity target = player.getTargetEntity(15, false) instanceof LivingEntity ?
                (LivingEntity) player.getTargetEntity(15, false) : null;

        if (target != null && target != player) {
            target.damage(8.0, player);
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
            player.sendMessage("§5Moonstrike hit " + target.getName() + "!");
        } else {
            player.sendMessage("§cNo target found!");
        }

        manager.setCooldown(player, "eclipserra_ability2", 35);
    }

    @Override
    public String getAbility1Name() {
        return "Lunar Veil";
    }

    @Override
    public String getAbility2Name() {
        return "Moonstrike";
    }
}