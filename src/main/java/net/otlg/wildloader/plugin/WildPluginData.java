package net.otlg.wildloader.plugin;

import lombok.Getter;
import net.otlg.mccorelib.utils.CommandUtils;
import net.otlg.wildloader.WildLoader;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

public class WildPluginData {
    @Getter
    private File origin;
    @Getter
    private URLClassLoader classLoader;
    @Getter
    private WildPluginDescription description;
    @Getter
    private File dataFolder;
    @Getter
    private List<Command> commands = new ArrayList<>();
    @Getter
    private List<Listener> eventListeners = new ArrayList<>();
    @Getter
    private List<Permission> permissions = new ArrayList<Permission>();

    public WildPluginData(File origin, URLClassLoader classLoader, WildPluginDescription description, File dataFolder) {
        this.origin = origin;
        this.classLoader = classLoader;
        this.description = description;
        this.dataFolder = dataFolder;
    }

    public void registerEvent(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, WildLoader.getInstance());
        eventListeners.add(listener);
    }

    public void unregisterEvent(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public void registerCommand(Command cmd) {
        CommandUtils.registerCommand(cmd);
        commands.add(cmd);
    }

    public void unregisterCommand(Command cmd) {
        if (commands.contains(cmd)) {
            CommandUtils.unregisterCommand(cmd);
        }
    }

    public void registerPermission(String permission) {
        Permission node = new Permission(permission);

        try {
            Bukkit.getPluginManager().addPermission(node);
            permissions.add(node);
        } catch (IllegalArgumentException e) {
            // Permission already registered
        }
    }

    public void unregisterPermission(String permission) {
        Iterator<Permission> permIterator = permissions.iterator();
        while (permIterator.hasNext()) {
            Permission perm = permIterator.next();

            if (perm.getName().equals(permission)) {
                Bukkit.getPluginManager().removePermission(perm);
                permIterator.remove();
            }
        }
    }


    public void destroy() {
        commands.forEach(this::unregisterCommand);
        eventListeners.forEach(HandlerList::unregisterAll);
        permissions.forEach(Bukkit.getPluginManager()::removePermission);
    }
}
