package com.bukkit.mcnestbuilder.plugin;

import com.bukkit.mcnestbuilder.Mediator;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Handle events for all Player related events
 * @author <yourname>
 */
public class MCNestBuilderPlayerListener extends PlayerListener {
    private final MCNestBuilder plugin;

    public MCNestBuilderPlayerListener(MCNestBuilder instance) {
        plugin = instance;
    }

    //Insert Player related code here
    @Override
    public void onPlayerCommand(PlayerChatEvent event) {
        // extract info
        String[] split = event.getMessage().substring(1).split(" ");
        Player player = event.getPlayer();
        
        String cmd = split[0];

        if (player.isOp()) {
            if (cmd.equalsIgnoreCase("buildnest")) {
                if (split.length > 2) {
                    player.sendMessage("USE: /buildnest <dimension:optional>\n\tdimension: The x and z dimension of the Termite bounding box.");
                }
                else {
                    int size = MCNestBuilder.DEFAULT_DIMENSION;

                    if (split.length == 2) {
                        size = Integer.valueOf(split[1]);
                    }

                    Mediator mediator = new Mediator();
                    mediator.setWorld(player.getWorld());

                    int x = player.getLocation().getBlockX();
                    int y = player.getLocation().getBlockY();
                    int z = player.getLocation().getBlockZ();

                    mediator.setCaller(player);
                    mediator.run(x, y, z, size);
                }
            }
            else if (cmd.equalsIgnoreCase("steve?")) {
                player.sendMessage("Nah, fuck steve...");
            }
        }
    }
}