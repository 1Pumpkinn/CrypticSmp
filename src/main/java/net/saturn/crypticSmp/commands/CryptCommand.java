package net.saturn.crypticSmp.commands;

import net.saturn.crypticSmp.crypt.CryptType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CryptCommand implements CommandExecutor, TabCompleter {
    private final net.saturn.crypticSmp.crypt.CryptManager cryptManager;

    public CryptCommand(net.saturn.crypticSmp.crypt.CryptManager cryptManager) {
        this.cryptManager = cryptManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "choose":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /crypt choose <crypt_name>");
                    return true;
                }
                handleChoose(player, args[1]);
                break;

            case "info":
                handleInfo(player);
                break;

            case "list":
                handleList(player);
                break;

            case "ability1":
            case "a1":
                handleAbility1(player);
                break;

            case "ability2":
            case "a2":
                handleAbility2(player);
                break;

            case "reset":
                handleReset(player);
                break;

            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void handleChoose(Player player, String cryptName) {
        try {
            CryptType crypt = CryptType.valueOf(cryptName.toUpperCase());
            cryptManager.setCrypt(player, crypt);
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cInvalid crypt name! Use /crypt list to see available crypts.");
        }
    }

    private void handleInfo(Player player) {
        if (!cryptManager.hasCrypt(player)) {
            player.sendMessage("§cYou haven't chosen a crypt yet! Use /crypt choose <name>");
            return;
        }

        CryptType crypt = cryptManager.getCrypt(player);
        player.sendMessage("§6=== Your Crypt ===");
        player.sendMessage("§e" + crypt.getDisplayName());
        player.sendMessage("§7Ability 1: §f" + crypt.getAbility1Name() + " §7(/crypt a1)");
        player.sendMessage("§7Ability 2: §f" + crypt.getAbility2Name() + " §7(/crypt a2)");
    }

    private void handleList(Player player) {
        player.sendMessage("§6=== Available Crypts ===");
        for (CryptType crypt : CryptType.values()) {
            player.sendMessage("§e" + crypt.name() + " §7- " + crypt.getDisplayName());
        }
        player.sendMessage("§7Use /crypt choose <name> to select a crypt");
    }

    private void handleAbility1(Player player) {
        if (!cryptManager.hasCrypt(player)) {
            player.sendMessage("§cYou haven't chosen a crypt yet!");
            return;
        }

        CryptType crypt = cryptManager.getCrypt(player);
        crypt.useAbility1(player, cryptManager);
    }

    private void handleAbility2(Player player) {
        if (!cryptManager.hasCrypt(player)) {
            player.sendMessage("§cYou haven't chosen a crypt yet!");
            return;
        }

        CryptType crypt = cryptManager.getCrypt(player);
        crypt.useAbility2(player, cryptManager);
    }

    private void handleReset(Player player) {
        if (!cryptManager.hasCrypt(player)) {
            player.sendMessage("§cYou don't have a crypt to reset!");
            return;
        }

        cryptManager.removeCrypt(player);
        player.sendMessage("§6Your crypt has been reset!");
    }

    private void sendHelp(Player player) {
        player.sendMessage("§6=== Crypt Commands ===");
        player.sendMessage("§e/crypt list §7- View all crypts");
        player.sendMessage("§e/crypt choose <name> §7- Choose a crypt");
        player.sendMessage("§e/crypt info §7- View your current crypt");
        player.sendMessage("§e/crypt a1 §7- Use ability 1");
        player.sendMessage("§e/crypt a2 §7- Use ability 2");
        player.sendMessage("§e/crypt reset §7- Reset your crypt");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("choose", "info", "list", "ability1", "ability2", "a1", "a2", "reset"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("choose")) {
            completions.addAll(Arrays.stream(CryptType.values())
                    .map(c -> c.name().toLowerCase())
                    .collect(Collectors.toList()));
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}