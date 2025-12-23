package net.saturn.crypticSmp.crypt;

import org.bukkit.entity.Player;

public interface CryptAbility {
    void applyPassive(Player player);
    void useAbility1(Player player, CryptManager manager);
    void useAbility2(Player player, CryptManager manager);
    String getAbility1Name();
    String getAbility2Name();
}