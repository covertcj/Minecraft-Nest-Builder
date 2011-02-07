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
public class NBPlayerListener extends PlayerListener {
    private final NestBuilder plugin;

    public NBPlayerListener(NestBuilder instance) {
        plugin = instance;
    }

    //Insert Player related code here
    @Override
    public void onPlayerCommand(PlayerChatEvent event) {
        try {
            // extract info
            String message = event.getMessage();
            Player player = event.getPlayer();

            String cmd = message.substring(1, 5);
            String sub = message.substring(6);

            player.sendMessage(cmd + ":" + sub);

            if (player.isOp()) {
                if (!cmd.equalsIgnoreCase("nest")) {
                    return;
                }

                player.sendMessage(sub.substring(0, 5));
                player.sendMessage(sub.substring(6));
                if (sub.substring(0, 5).equalsIgnoreCase("build")) {
                    String[] args = sub.substring(6).split(" ");

                    if (args.length > 2) {
                        player.sendMessage("USE: /nest build <dimension:optional> <timestep:optional>\ndimension: The x and z dimension of the Termite bounding box.\ndimestep: The amount of time (in mili) between each termite action.");
                    }
                    else {
                        int size = NestBuilder.DEFAULT_DIMENSION;
                        int timestep = NestBuilder.DEFAULT_TIMESTEP;

                        if (args.length >= 1) {
                            size = Integer.valueOf(args[0]);
                        }

                        if (args.length >= 2) {
                            timestep = Integer.valueOf(args[1]);
                        }

                        Mediator mediator = new Mediator(player, size, timestep);
                        Thread thread = new Thread(mediator, player.getName() + "NestBuilderThread");
                        thread.start();

                        event.setCancelled(true);
                    }
                }
            }
        } catch (StringIndexOutOfBoundsException ex) {
            return;
        }
    }
}