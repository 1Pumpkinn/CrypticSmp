package net.saturn.crypticSmp;

import net.saturn.crypticSmp.commands.CryptCommand;
import net.saturn.crypticSmp.listeners.CryptListener;
import net.saturn.crypticSmp.crypt.CryptManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CrypticSmp extends JavaPlugin {

    private static CrypticSmp instance;
    private CryptManager cryptManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize crypt manager
        cryptManager = new CryptManager(this);

        // Register commands
        CryptCommand cryptCommand = new CryptCommand(cryptManager);
        getCommand("crypt").setExecutor(cryptCommand);
        getCommand("crypt").setTabCompleter(cryptCommand);

        // Register listeners
        getServer().getPluginManager()
                .registerEvents(new CryptListener(cryptManager), this);

        getLogger().info("CrypticSmp has been enabled!");
        getLogger().info("8 Crypts loaded successfully!");
    }

    @Override
    public void onDisable() {
        instance = null;
        getLogger().info("CrypticSmp has been disabled!");
    }

    public static CrypticSmp getInstance() {
        return instance;
    }

    public CryptManager getCryptManager() {
        return cryptManager;
    }
}
