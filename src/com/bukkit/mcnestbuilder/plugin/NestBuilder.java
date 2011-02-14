/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder.plugin;

/**
 *
 * @author covertcj
 */
import com.bukkit.mcnestbuilder.Settings;
import com.bukkit.mcnestbuilder.TermiteDestructor;
import java.io.File;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Sample plugin for Bukkit
 *
 * @author Dinnerbone
 */
public class NestBuilder extends JavaPlugin {
    private final NBPlayerListener playerListener = new NBPlayerListener(this);
//    private final BlockListener blockListener = new BlockListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

    public static final int     DEFAULT_DIMENSION   = 128;
    public static final int     DEFAULT_DURATION    = 10;

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

        // register events
        pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);

        // schedule events
        sched.scheduleSyncRepeatingTask(this, new TermiteDestructor(), 50, 50);

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
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