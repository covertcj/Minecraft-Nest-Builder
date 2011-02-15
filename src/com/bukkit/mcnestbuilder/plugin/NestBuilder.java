/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder.plugin;

/**
 *
 * @author covertcj
 */
import com.bukkit.mcnestbuilder.Mediator;
import com.bukkit.mcnestbuilder.Settings;
import com.bukkit.mcnestbuilder.TermiteManager;
import java.io.File;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public class NestBuilder extends JavaPlugin {

    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

    public NestBuilder(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
        // TODO: Place any custom initialisation code here

        // NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
    }

    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        System.out.println("Goodbye world!");
    }

    public void onEnable() {

        PluginManager pm = getServer().getPluginManager();
        BukkitScheduler sched = getServer().getScheduler();
        Settings.Initialize();

        // schedule events
        sched.scheduleSyncRepeatingTask(this, new TermiteManager(), 50, 50);

        // print load verification message
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String cmdName = command.getName();
        Player player = (Player) sender;

        if (cmdName.equalsIgnoreCase("nest")) {
            if (args.length == 2) {
                // setup the AI
                int size = Integer.valueOf(args[0]);
                int timestep = Integer.valueOf(args[1]);

                    Mediator mediator = new Mediator(player, size, timestep);
                    if (!mediator.InitializeTermtites()) {
                        player.sendMessage("Error: There are too many NPC's, wait for some to despawn.");
                    }

                    // run the AI
                    Thread thread = new Thread(mediator, player.getName() + "NestBuilderThread");
                    thread.start();
            }
        }
        else if (cmdName.equalsIgnoreCase("nestundo")) {
            Mediator.undo();
        }

        return false;
    }

    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
}