package net.otlg.wildloader;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class WildLoader extends JavaPlugin {

    private static WildLoader wildLoader;
    private PluginManager pluginManager = new PluginManager(getDataFolder(), getLogger());
    private SessionStorage storage = new SessionStorage();

    public static PluginManager getPluginManager() {
        return wildLoader.pluginManager;
    }

    public static SessionStorage getSessionStorage() {
        return wildLoader.storage;
    }

    public static WildLoader getInstance() {
        return wildLoader;
    }

    @Override
    public void onEnable() {
        wildLoader = this;
        getCommand("wildloader").setExecutor(new WildLoaderCommand(this));
        pluginManager.loadAll();
    }

    @Override
    public void onDisable() {
        pluginManager.unloadAll();
    }
}
