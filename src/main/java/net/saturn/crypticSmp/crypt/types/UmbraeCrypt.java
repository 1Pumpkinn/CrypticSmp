package net.saturn.crypticSmp.crypt.types;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import net.saturn.crypticSmp.CrypticSmp;
import net.saturn.crypticSmp.crypt.CryptAbility;
import net.saturn.crypticSmp.crypt.CryptManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class UmbraeCrypt implements CryptAbility {

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
            player.sendMessage("§cShadow Clone is on cooldown for "
                    + manager.getRemainingCooldown(player, "umbrae_ability1") + "s");
            return;
        }

        Location cloneLoc = player.getLocation().clone();
        int entityId = new Random().nextInt(100_000) + 1_000;
        UUID fakeUUID = UUID.randomUUID();

        try {
            WrappedGameProfile wrappedProfile =
                    new WrappedGameProfile(fakeUUID, player.getName());

            WrappedGameProfile realProfile =
                    WrappedGameProfile.fromPlayer(player);

            wrappedProfile.getProperties().putAll(realProfile.getProperties());

            PacketContainer infoPacket =
                    ProtocolLibrary.getProtocolManager()
                            .createPacket(PacketType.Play.Server.PLAYER_INFO);

            infoPacket.getPlayerInfoActions().write(
                    0,
                    EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER)
            );

            List<PlayerInfoData> data = new ArrayList<>();
            data.add(new PlayerInfoData(
                    wrappedProfile,
                    0,
                    EnumWrappers.NativeGameMode.SURVIVAL,
                    WrappedChatComponent.fromText(player.getName())
            ));

            infoPacket.getPlayerInfoDataLists().write(1, data);

            PacketContainer spawnPacket =
                    ProtocolLibrary.getProtocolManager()
                            .createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);

            spawnPacket.getIntegers().write(0, entityId);
            spawnPacket.getUUIDs().write(0, fakeUUID);
            spawnPacket.getDoubles()
                    .write(0, cloneLoc.getX())
                    .write(1, cloneLoc.getY())
                    .write(2, cloneLoc.getZ());
            spawnPacket.getBytes()
                    .write(0, (byte) (cloneLoc.getYaw() * 256 / 360))
                    .write(1, (byte) (cloneLoc.getPitch() * 256 / 360));

            for (Player other : player.getWorld().getPlayers()) {
                if (other.equals(player)) continue;

                ProtocolLibrary.getProtocolManager()
                        .sendServerPacket(other, infoPacket);
                ProtocolLibrary.getProtocolManager()
                        .sendServerPacket(other, spawnPacket);

                other.hidePlayer(CrypticSmp.getInstance(), player);
            }

            player.sendMessage("§8Shadow Clone active! You have 5 seconds to move.");

            new BukkitRunnable() {
                @Override
                public void run() {

                    if (!player.isOnline()) return;

                    PacketContainer destroyPacket =
                            ProtocolLibrary.getProtocolManager()
                                    .createPacket(PacketType.Play.Server.ENTITY_DESTROY);

                    destroyPacket.getIntLists()
                            .write(0, Collections.singletonList(entityId));

                    PacketContainer removeInfoPacket =
                            ProtocolLibrary.getProtocolManager()
                                    .createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);

                    removeInfoPacket.getUUIDLists()
                            .write(0, Collections.singletonList(fakeUUID));

                    for (Player other : player.getWorld().getPlayers()) {
                        if (other.equals(player)) continue;

                        ProtocolLibrary.getProtocolManager()
                                .sendServerPacket(other, destroyPacket);
                        ProtocolLibrary.getProtocolManager()
                                .sendServerPacket(other, removeInfoPacket);

                        other.showPlayer(CrypticSmp.getInstance(), player);
                    }

                    player.sendMessage("§8Shadow Clone ended!");
                }
            }.runTaskLater(CrypticSmp.getInstance(), 100L);

        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage("§cFailed to create shadow clone!");
            return;
        }

        manager.setCooldown(player, "umbrae_ability1", 75);
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
        return "Shadow Clone";
    }

    @Override
    public String getAbility2Name() {
        return "Fade Step";
    }
}
