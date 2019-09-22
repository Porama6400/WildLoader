package net.otlg.wildloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.otlg.wildloader.plugin.WildPlugin;
import net.otlg.wildloader.plugin.WildPluginData;
import net.otlg.wildloader.plugin.WildPluginDescription;
import net.otlg.wildloader.plugin.mode.ShutdownMode;
import net.otlg.wildloader.plugin.mode.StartMode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class PluginManager {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Getter
    private Logger logger;

    @Getter
    private List<WildPlugin> plugins = new ArrayList<>();

    @Getter
    private File workingDirectory;

    public PluginManager(File workingDirectory, Logger logger) {
        this.workingDirectory = workingDirectory;
        workingDirectory.mkdirs();
        this.logger = logger;
    }

    public void load(String fileName, StartMode mode) {
        load(new File(workingDirectory, fileName + ".jar"), mode);
    }

    public void load(File jar, StartMode mode) {
        try {
            // Load Jar file
            URLClassLoader loader = new URLClassLoader(new URL[]{jar.toURI().toURL()}, this.getClass().getClassLoader());

            // Load plugin description
            InputStream stream = loader.getResourceAsStream("wild.json");
            byte[] pluginDescriptionBinary = new byte[stream.available()];
            stream.read(pluginDescriptionBinary);
            stream.close();
            WildPluginDescription description = gson.fromJson(new String(pluginDescriptionBinary), WildPluginDescription.class);

            getLogger().info("Loading " + description.name + " " + description.version + " by " + description.author);

            // Get the class
            //Class pluginClass = loader.loadClass(description.mainClass);
            Class pluginClass = Class.forName(description.mainClass, true, loader);

            WildPlugin plugin = (WildPlugin) pluginClass.newInstance();
            plugin.setData(new WildPluginData(jar, loader, description, new File(workingDirectory, description.name)));
            plugin.onStart(mode);
            plugins.add(plugin);

        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public WildPlugin getPlugin(String name) {
        for (WildPlugin plugin : plugins) {
            if (plugin.getName().equals(name)) return plugin;
        }
        return null;
    }

    public void unload(WildPlugin plugin, ShutdownMode mode) {
        WildPluginDescription description = plugin.getDescription();
        getLogger().info("Unloading " + description.name + " " + description.version + " by " + description.author);

        plugin.onShutdown(mode);
        plugin.getData().destroy();
        System.gc();
        plugins.remove(plugin);
    }

    public void restart(WildPlugin plugin) {
        restart(plugin, null);
    }

    public void restart(WildPlugin plugin, File newJar) {
        unload(plugin, ShutdownMode.RESTART);

        if (newJar == null)
            load(plugin.getData().getOrigin(), StartMode.RESTART);
        else
            load(newJar, StartMode.RESTART);

        plugins.remove(plugin);
    }

    public void loadAll() {
        for (File file : Objects.requireNonNull(getWorkingDirectory().listFiles())) {
            if (file.isDirectory()) continue;
            if (!file.getName().toLowerCase().endsWith(".jar")) continue;
            load(file, StartMode.START);
        }
    }

    public void unloadAll() {
        while (plugins.size() > 0) {
            unload(plugins.get(0), ShutdownMode.SHUTDOWN);
        }
    }
}
