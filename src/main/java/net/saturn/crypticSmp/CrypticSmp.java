package net.saturn.crypticSmp;

import net.saturn.crypticSmp.commands.CryptCommand;
import net.saturn.crypticSmp.listeners.CryptListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class CrypticSmp extends JavaPlugin {
    private net.saturn.crypticSmp.crypt.CryptManager cryptManager;

    @Override
    public void onEnable() {
        // Initialize crypt manager
        cryptManager = new net.saturn.crypticSmp.crypt.CryptManager(this);

        // Register commands
        CryptCommand cryptCommand = new CryptCommand(cryptManager);
        getCommand("crypt").setExecutor(cryptCommand);
        getCommand("crypt").setTabCompleter(cryptCommand);

        // Register listeners
        getServer().getPluginManager().registerEvents(new net.saturn.crypticSmp.listeners.CryptListener(cryptManager), this);

        getLogger().info("CrypticSmp has been enabled!");
        getLogger().info("8 Crypts loaded successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("CrypticSmp has been disabled!");
    }

    public net.saturn.crypticSmp.crypt.CryptManager getCryptManager() {
        return cryptManager;
    }
}