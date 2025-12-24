package net.saturn.crypticSmp.listeners;

import net.saturn.crypticSmp.crypt.CryptManager;
import net.saturn.crypticSmp.crypt.CryptType;
import net.saturn.crypticSmp.crypt.types.ThundrosCrypt;
import net.saturn.crypticSmp.crypt.types.UmbraeCrypt;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Check if player is stunned by Umbrae's Shadow Grasp
        if (UmbraeCrypt.isStunned(player.getUniqueId())) {
            // Cancel all movement
            if (event.getFrom().getX() != event.getTo().getX() ||
                    event.getFrom().getY() != event.getTo().getY() ||
                    event.getFrom().getZ() != event.getTo().getZ()) {
                event.setTo(event.getFrom());
            }

            // Lock camera rotation
            if (event.getFrom().getYaw() != event.getTo().getYaw() ||
                    event.getFrom().getPitch() != event.getTo().getPitch()) {
                event.getTo().setYaw(event.getFrom().getYaw());
                event.getTo().setPitch(event.getFrom().getPitch());
            }

            // Set velocity to zero to prevent any physics-based movement
            player.setVelocity(new Vector(0, 0, 0));
        }
    }
}