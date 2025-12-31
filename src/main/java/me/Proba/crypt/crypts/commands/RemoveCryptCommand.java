package me.Proba.crypt.crypts.commands;

import me.Proba.crypt.CryptJavaPlugin;
import me.Proba.crypt.crypts.crypts.CryptManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RemoveCryptCommand implements CommandExecutor {

    private CryptJavaPlugin plugin;

    public RemoveCryptCommand() {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        CryptManager manager = plugin.getCryptManager();
        boolean removedAny = false;

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) continue;

            // Any item named "<Something> Crypt" counts
            String strippedName = ChatColor.stripColor(meta.getDisplayName());
            if (strippedName.endsWith("Crypt")) {
                player.getInventory().setItem(i, null);
                removedAny = true;

                // Remove from CryptManager tracking
                String cryptName = strippedName.replace(" Crypt", "").toLowerCase();
                manager.removeCrypt(player, cryptName);
            }
        }

        if (removedAny) {
            player.sendMessage(ChatColor.GREEN + "All crypts have been removed.");
        } else {
            player.sendMessage(ChatColor.YELLOW + "You don't have any crypts to remove.");
        }

        return true;
    }
}
