package com.bukkit.mcnestbuilder.plugin;

import com.bukkit.mcnestbuilder.Mediator;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.npcspawner.BasicHumanNpc;
import org.bukkit.npcspawner.BasicHumanNpcList;
import org.bukkit.npcspawner.NpcSpawner;

/**
 * Handle events for all Player related events
 * @author <yourname>
 */
public class NBPlayerListener extends PlayerListener {
    private final NestBuilder plugin;

    BasicHumanNpcList npcs;

    public NBPlayerListener(NestBuilder instance) {
        plugin = instance;
        npcs = new BasicHumanNpcList();
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

            if (player.isOp()) {
                if (!cmd.equalsIgnoreCase("nest")) {
                    return;
                }

                player.sendMessage(sub.substring(0, 5));
                player.sendMessage(sub.substring(6));
                if (sub.substring(0, 5).equalsIgnoreCase("build")) {
                    String[] args = {};

                    if (sub.length() > 5) {
                        args = sub.substring(6).split(" ");
                    }

                    if (args.length > 2) {
                        player.sendMessage("USE: /nest build <dimension:optional> <timestep:optional>\ndimension: The x and z dimension of the Termite bounding box.\ndimestep: The amount of time (in mili) between each termite action.");
                    }
                    else {
                        int size = NestBuilder.DEFAULT_DIMENSION;
                        int timestep = NestBuilder.DEFAULT_DURATION;

                        if (args.length >= 1) {
                            size = Integer.valueOf(args[0]);
                        }

                        if (args.length >= 2) {
                            timestep = Integer.valueOf(args[1]);
                        }

                        // setup the AI
                        Mediator mediator = new Mediator(player, size, timestep);
                        if (!mediator.InitializeTermtites()) {
                            player.sendMessage("Error: There are too many NPC's, wait for some to despawn.");
                        }

                        // run the AI
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