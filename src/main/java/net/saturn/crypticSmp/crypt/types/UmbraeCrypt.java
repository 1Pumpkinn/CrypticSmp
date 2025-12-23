package net.saturn.crypticSmp.crypt.types;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 0, true, false));
        }
    }

    @Override
    public void useAbility1(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "umbrae_ability1")) {
            player.sendMessage("§cShadow Clone is on cooldown for " + manager.getRemainingCooldown(player, "umbrae_ability1") + "s");
            return;
        }

        Location cloneLoc = player.getLocation().clone();
        int entityId = new Random().nextInt(10000) + 100000;
        UUID fakeUUID = UUID.randomUUID();

        try {
            // Create GameProfile with player's skin
            GameProfile profile = new GameProfile(fakeUUID, player.getName());

            // Copy skin properties
            for (Property property : player.getPlayerProfile().getProperties()) {
                profile.getProperties().put(property.getName(), property);
            }

            // Create wrapped game profile
            WrappedGameProfile wrappedProfile = WrappedGameProfile.fromHandle(profile);

            // Send player info packet to add the fake player to tab list
            PacketContainer infoPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
            infoPacket.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));

            List<PlayerInfoData> data = new ArrayList<>();
            data.add(new PlayerInfoData(
                    wrappedProfile,
                    0,
                    EnumWrappers.NativeGameMode.SURVIVAL,
                    WrappedChatComponent.fromText(player.getName())
            ));
            infoPacket.getPlayerInfoDataLists().write(1, data);

            // Spawn the fake player
            PacketContainer spawnPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
            spawnPacket.getIntegers().write(0, entityId);
            spawnPacket.getUUIDs().write(0, fakeUUID);
            spawnPacket.getDoubles()
                    .write(0, cloneLoc.getX())
                    .write(1, cloneLoc.getY())
                    .write(2, cloneLoc.getZ());
            spawnPacket.getBytes()
                    .write(0, (byte) (cloneLoc.getYaw() * 256.0F / 360.0F))
                    .write(1, (byte) (cloneLoc.getPitch() * 256.0F / 360.0F));

            // Send packets to all nearby players
            for (Player other : player.getWorld().getPlayers()) {
                if (!other.equals(player)) {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(other, infoPacket);
                    ProtocolLibrary.getProtocolManager().sendServerPacket(other, spawnPacket);

                    // Hide the real player
                    other.hidePlayer(player.getServer().getPluginManager().getPlugin("CrypticSmp"), player);
                }
            }

            player.sendMessage("§8Shadow Clone active! You have 5 seconds to move.");

            // After 5 seconds, remove the fake player and reveal the real one
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) return;

                    try {
                        // Remove entity packet
                        PacketContainer destroyPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
                        destroyPacket.getIntLists().write(0, Collections.singletonList(entityId));

                        // Remove from tab list
                        PacketContainer removeInfoPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
                        removeInfoPacket.getUUIDLists().write(0, Collections.singletonList(fakeUUID));

                        // Send to all nearby players
                        for (Player other : player.getWorld().getPlayers()) {
                            if (!other.equals(player)) {
                                ProtocolLibrary.getProtocolManager().sendServerPacket(other, destroyPacket);
                                ProtocolLibrary.getProtocolManager().sendServerPacket(other, removeInfoPacket);

                                // Show the real player at their new location
                                other.showPlayer(player.getServer().getPluginManager().getPlugin("CrypticSmp"), player);
                            }
                        }

                        player.sendMessage("§8Shadow Clone ended! You remain at your current location.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskLater(player.getServer().getPluginManager().getPlugin("CrypticSmp"), 100L);

        } catch (Exception e) {
            player.sendMessage("§cFailed to create shadow clone!");
            e.printStackTrace();
            return;
        }

        manager.setCooldown(player, "umbrae_ability1", 75);
    }

    @Override
    public void useAbility2(Player player, CryptManager manager) {
        if (manager.isOnCooldown(player, "umbrae_ability2")) {
            player.sendMessage("§cFade Step is on cooldown for " + manager.getRemainingCooldown(player, "umbrae_ability2") + "s");
            return;
        }

        Vector direction = player.getLocation().getDirection().normalize().multiply(15);
        Location newLoc = player.getLocation().add(direction);

        // Keep the original yaw and pitch
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();
        newLoc.setYaw(yaw);
        newLoc.setPitch(pitch);

        // Ensure player doesn't teleport into walls
        if (newLoc.getBlock().isPassable()) {
            player.teleport(newLoc);
            player.sendMessage("§8Fade Step!");
        } else {
            player.sendMessage("§cCannot teleport into solid blocks!");
            return;
        }

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