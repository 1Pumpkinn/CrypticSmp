package me.Proba.crypt.crypts.crypts;

import me.Proba.crypt.CryptJavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CryptManager implements Listener {

    private final CryptJavaPlugin plugin;

    // Track all equipped crypts globally
    private final Map<Player, Set<String>> equippedCrypts = new HashMap<>();

    public CryptManager(CryptJavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        // Pass main plugin to each crypt
        new Solari(plugin).enable();
        new Umbrae(plugin).enable();
        new Terrakai(plugin).enable();
        new Zephara(plugin).enable();
        new Nytheris(plugin).enable();
        new Aquarion(plugin).enable();
        new Thundros(plugin).enable();
        new Eclipserra(plugin).enable();
    }

    public CryptJavaPlugin getPlugin() {
        return plugin;
    }

    /* =========================
       Checks if the player has the specified crypt equipped in main hand
       ========================= */
    public boolean hasCryptEquipped(Player player, String cryptName) {
        // Check main hand first
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                String displayName = ChatColor.stripColor(meta.getDisplayName());
                if (displayName.equalsIgnoreCase(cryptName + " Crypt")) return true;
            }
        }

        // Check global equipped list
        return equippedCrypts.containsKey(player) &&
                equippedCrypts.get(player).contains(cryptName.toLowerCase());
    }

    /* =========================
       Checks if the player has any crypt equipped in main hand
       ========================= */
    public boolean hasCrypt(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return false;

        return meta.getDisplayName().endsWith(" Crypt");
    }

    /* =========================
       Equip a crypt globally for a player
       ========================= */
    public void equipCrypt(Player player, String cryptName) {
        cryptName = cryptName.toLowerCase();
        equippedCrypts.computeIfAbsent(player, k -> new HashSet<>()).add(cryptName);
    }

    /* =========================
       Remove a specific crypt from the player
       ========================= */
    public void removeCrypt(Player player, String cryptName) {
        // Remove from main hand if matching
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName() &&
                    meta.getDisplayName().equalsIgnoreCase(cryptName + " Crypt")) {
                player.getInventory().setItemInMainHand(null);
            }
        }

        // Remove from global tracking
        cryptName = cryptName.toLowerCase();
        if (equippedCrypts.containsKey(player)) {
            equippedCrypts.get(player).remove(cryptName);
            if (equippedCrypts.get(player).isEmpty()) {
                equippedCrypts.remove(player);
            }
        }

        player.sendMessage(ChatColor.GREEN + cryptName + " Crypt removed.");
    }

    /* =========================
       Remove all crypts from the player
       ========================= */
    public void removeAllCrypts(Player player) {
        // Remove main-hand crypt if present
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName() && meta.getDisplayName().endsWith(" Crypt")) {
                player.getInventory().setItemInMainHand(null);
            }
        }

        // Clear global tracking
        equippedCrypts.remove(player);
        player.sendMessage(ChatColor.GREEN + "All your crypts have been removed.");
    }
}
