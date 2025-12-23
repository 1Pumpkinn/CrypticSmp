package net.saturn.crypticSmp.listeners;

import net.saturn.crypticSmp.crypt.CryptManager;
import net.saturn.crypticSmp.crypt.CryptType;
import net.saturn.crypticSmp.crypt.types.ThundrosCrypt;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CryptListener implements Listener {
    private final CryptManager cryptManager;

    public CryptListener(CryptManager cryptManager) {
        this.cryptManager = cryptManager;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();

        if (!cryptManager.hasCrypt(player)) return;

        CryptType crypt = cryptManager.getCrypt(player);

        // Handle Thundros passive (20% chance to strike attacker with lightning)
        if (crypt == CryptType.THUNDROS) {
            ThundrosCrypt thundros = new ThundrosCrypt();
            thundros.handlePassiveStrike(event, player);
        }
    }
}