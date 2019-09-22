package net.otlg.wildloader;

import net.otlg.wildloader.plugin.WildPlugin;
import net.otlg.wildloader.plugin.mode.ShutdownMode;
import net.otlg.wildloader.plugin.mode.StartMode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;

public class WildLoaderCommand implements CommandExecutor {
    private WildLoader wildLoader;

    public WildLoaderCommand(WildLoader wildLoader) {
        this.wildLoader = wildLoader;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        PluginManager pluginManager = WildLoader.getPluginManager();

        if (args.length == 0) {

        } else if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "list":
                    sender.sendMessage(ChatColor.GREEN + "List of loaded plugins:");
                    sender.sendMessage(ChatColor.GREEN + "==================================");
                    for (WildPlugin plugin : pluginManager.getPlugins()) {
                        sender.sendMessage(plugin.getDescription().name);
                    }
                    sender.sendMessage(ChatColor.GREEN + "==================================");
                    break;
            }
        } else {
            switch (args[0].toLowerCase()) {
                case "load":
                    File pluginFile = new File(pluginManager.getWorkingDirectory(), args[1] + ".jar");
                    if (!pluginFile.exists()) {
                        sender.sendMessage(ChatColor.RED + "File not found (" + pluginFile.getPath() + ")");
                        return true;
                    }
                    pluginManager.load(pluginFile, StartMode.START);
                    sender.sendMessage(ChatColor.GREEN + args[1] + " loaded!");
                    return true;
                case "unload":
                    if (pluginManager.getPlugin(args[1]) == null) {
                        sender.sendMessage(ChatColor.RED + "Plugin not found (" + args[1] + ")");
                        return false;
                    }

                    pluginManager.unload(pluginManager.getPlugin(args[1]), ShutdownMode.SHUTDOWN);
                    sender.sendMessage(ChatColor.GREEN + args[1] + " unloaded!");
                    return true;
                case "reload":
                    if (pluginManager.getPlugin(args[1]) == null) {
                        sender.sendMessage(ChatColor.GREEN + "Plugin not found (" + args[1] + ")");
                        return false;
                    }

                    File newJar = null;
                    if (args.length > 2) {
                        newJar = new File(WildLoader.getPluginManager().getWorkingDirectory(), args[2] + ".jar");
                        if (!newJar.exists()) {
                            sender.sendMessage(ChatColor.YELLOW + "Specified Jar does not exist, using old one...");
                            newJar = null;
                        }
                    }
                    pluginManager.restart(pluginManager.getPlugin(args[1]), newJar);
                    sender.sendMessage(ChatColor.GREEN + args[1] + " reloaded!");
                    return true;

            }
        }

        return true;
    }
}
