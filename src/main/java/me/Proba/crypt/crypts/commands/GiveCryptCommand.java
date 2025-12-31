package me.Proba.crypt.crypts.commands;

import me.Proba.crypt.CryptJavaPlugin;
import me.Proba.crypt.crypts.crypts.CryptManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.List;

public class GiveCryptCommand implements CommandExecutor {

    private CryptJavaPlugin plugin = null;
    private final List<String> crypts = Arrays.asList(
            "solari",
            "umbrae",
            "terrakai",
            "zephara",
            "nytheris",
            "aquarion",
            "thundros",
            "eclipserra"
    );

    public GiveCryptCommand() {
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        CryptManager manager = plugin.getCryptManager();

        // No args → list available crypts
        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Available crypts:");
            player.sendMessage(ChatColor.GOLD + String.join(", ", crypts));
            return true;
        }

        String cryptName = args[0].toLowerCase();

        if (!crypts.contains(cryptName)) {
            player.sendMessage(ChatColor.RED + "Invalid crypt.");
            player.sendMessage(ChatColor.YELLOW + "Available: " + String.join(", ", crypts));
            return true;
        }

        // Create the crypt item
        ItemStack cryptItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = cryptItem.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + capitalize(cryptName) + " Crypt");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "An ancient crypt artifact",
                    ChatColor.GRAY + "Hold this to wield its power"
            ));
            cryptItem.setItemMeta(meta);
        }

        player.getInventory().addItem(cryptItem);
        player.sendMessage(ChatColor.GREEN + "You received the " + capitalize(cryptName) + " Crypt!");

        // Register the crypt in the CryptManager so abilities and /removecrypt work
        manager.equipCrypt(player, cryptName);

        return true;
    }

    private String capitalize(String s) {
        if (s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
