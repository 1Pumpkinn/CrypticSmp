package me.Proba.crypt.crypts.commands;

import me.Proba.crypt.CryptJavaPlugin;
import me.Proba.crypt.crypts.crypts.*;
import me.Proba.crypt.crypts.crypts.CryptManager;
import me.Proba.crypt.crypts.utils.CooldownUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AbilityCommand implements CommandExecutor {

    private final CryptJavaPlugin plugin;

    public AbilityCommand(CryptJavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Players only.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /ability <1|2>");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (!isCryptItem(item)) {
            player.sendMessage(ChatColor.RED + "You must be holding a Crypt.");
            return true;
        }

        String cryptName = getCryptName(item);
        CryptManager manager = plugin.getCryptManager();

        // Ensure player actually has the crypt equipped
        if (!manager.hasCryptEquipped(player, cryptName)) {
            player.sendMessage(ChatColor.RED + "You do not have that crypt equipped.");
            return true;
        }

        int ability;
        try {
            ability = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Ability must be 1 or 2.");
            return true;
        }

        switch (cryptName) {

            case "solari" -> {
                Solari solari = new Solari(plugin);
                if (ability == 1) {
                    if (CooldownUtil.isOnCooldown(player, "solari_flare")) {
                        long remaining = CooldownUtil.getRemaining(player, "solari_flare");
                        player.sendMessage(ChatColor.RED + "Solar Flare is on cooldown (" + remaining / 1000 + "s).");
                    } else solari.solarFlare(player);
                } else if (ability == 2) {
                    if (CooldownUtil.isOnCooldown(player, "solari_sunbeam")) {
                        long remaining = CooldownUtil.getRemaining(player, "solari_sunbeam");
                        player.sendMessage(ChatColor.RED + "Sunbeam is on cooldown (" + remaining / 1000 + "s).");
                    } else solari.sunbeam(player);
                } else player.sendMessage(ChatColor.RED + "Invalid ability.");
            }

            case "thundros" -> {
                Thundros thundros = new Thundros(plugin);
                if (ability == 1) {
                    if (CooldownUtil.isOnCooldown(player, "thundros_lightning")) {
                        long remaining = CooldownUtil.getRemaining(player, "thundros_lightning");
                        player.sendMessage(ChatColor.RED + "Lightning Call is on cooldown (" + remaining / 1000 + "s).");
                    } else thundros.lightningCall(player);
                } else if (ability == 2) {
                    if (CooldownUtil.isOnCooldown(player, "thundros_storm")) {
                        long remaining = CooldownUtil.getRemaining(player, "thundros_storm");
                        player.sendMessage(ChatColor.RED + "Storm Burst is on cooldown (" + remaining / 1000 + "s).");
                    } else thundros.stormBurst(player);
                } else player.sendMessage(ChatColor.RED + "Invalid ability.");
            }

            case "zephara" -> {
                Zephara zephara = new Zephara(plugin);
                if (ability == 1) {
                    if (CooldownUtil.isOnCooldown(player, "zephara_dash")) {
                        long remaining = CooldownUtil.getRemaining(player, "zephara_dash");
                        player.sendMessage(ChatColor.RED + "Gale Dash is on cooldown (" + remaining / 1000 + "s).");
                    } else zephara.galeDash(player);
                } else if (ability == 2) {
                    if (CooldownUtil.isOnCooldown(player, "zephara_rise")) {
                        long remaining = CooldownUtil.getRemaining(player, "zephara_rise");
                        player.sendMessage(ChatColor.RED + "Wind Rise is on cooldown (" + remaining / 1000 + "s).");
                    } else zephara.windRise(player);
                } else player.sendMessage(ChatColor.RED + "Invalid ability.");
            }

            // TODO: Add other crypts like Umbrae, Terrakai, etc.

            default -> player.sendMessage(ChatColor.RED + "Unknown crypt.");
        }

        return true;
    }

    // ---------- Helpers ----------

    private boolean isCryptItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().endsWith(" Crypt");
    }

    private String getCryptName(ItemStack item) {
        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        return name.replace(" Crypt", "").toLowerCase();
    }
}
