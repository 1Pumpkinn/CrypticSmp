package me.Proba.crypt;

import me.Proba.crypt.crypts.crypts.CryptManager;
import me.Proba.crypt.crypts.commands.AbilityCommand;
import me.Proba.crypt.crypts.commands.GiveCryptCommand;
import me.Proba.crypt.crypts.commands.RemoveCryptCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class CryptJavaPlugin extends JavaPlugin {

    private CryptManager cryptManager;

    @Override
    public void onEnable() {
        getLogger().info("Crypts Plugin Enabled!");

        // Initialize CryptManager with main plugin
        cryptManager = new CryptManager(this);
        cryptManager.registerAll();

        // Register events
        getServer().getPluginManager().registerEvents(cryptManager, this);

        // Register commands
        getCommand("ability").setExecutor(new AbilityCommand(this));
        getCommand("givecrypt").setExecutor(new GiveCryptCommand());
        getCommand("removecrypt").setExecutor(new RemoveCryptCommand());
    }

    @Override
    public void onDisable() {
        getLogger().info("Crypts Plugin Disabled!");
    }

    public CryptManager getCryptManager() {
        return cryptManager;
    }
}
