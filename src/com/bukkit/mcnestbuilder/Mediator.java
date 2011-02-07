/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder;

import com.bukkit.mcnestbuilder.ai.BuilderTermite;
import com.bukkit.mcnestbuilder.ai.QueenTermite;
import com.bukkit.mcnestbuilder.ai.Termite;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.entity.Player;

/**
 *
 * @author covertcj
 */
public class Mediator implements Runnable {

    Player caller;
    World world;
    WorldData worldData;

    ArrayList<Termite> termites;

    int dimension;
    int timestep;

    static final int NPC_MAX = 500;
    static int npcID;
    static int npcCount;

    public Mediator(Player player, int dimension, int timestep) {

        this.caller = player;
        this.world = this.caller.getWorld();
        this.worldData = new WorldData(this.world);

        this.dimension = dimension;
        this.timestep = timestep;

        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();

        this.caller.sendMessage("Initializing Nest Builder at (" + x + ", " + y + ", " + z + ") with a dimension of " + dimension + "...");

        termites = new ArrayList<Termite>();

        // create the termites
        InitializeTermtites();
    }

    /**
     * Runs the Nest Builder. Should be used as a separate thread.
     */
    public void run() {

        boolean done = false;
        int count = 0;

        // TODO: Run the bot
        while (!done) {

            for (Termite termite : termites) {
                termite.act();
            }

//            for (Termite termite : termites) {
//                termite.layPheromone();
//            }
            
            try {
              Thread.sleep(this.timestep);
            } catch (InterruptedException ex) {
                Logger.getLogger(Mediator.class.getName()).log(Level.SEVERE, null, ex);
            }

            // temporary limit to how long it runs
            count++;
            if (count >= 100) {
                done = true;
            }
        }

        // cleanup
        destroyTermites();
        this.caller.sendMessage("Nest Building process has completed.");
    }

    private boolean InitializeTermtites() {
        try {
            int queenX = this.caller.getLocation().getBlockX();
            int queenY = this.caller.getLocation().getBlockY();
            int queenZ = this.caller.getLocation().getBlockZ();

            int xOrigin = queenX - dimension / 2;
            int zOrigin = queenZ - dimension / 2;

            termites.add(new QueenTermite(queenX, queenY, queenZ, worldData));

            for (int x = xOrigin; x < dimension + xOrigin; x++) {
                for (int z = zOrigin; z < dimension + zOrigin; z++) {
                    for (int y = 0; y < 128; y++) {

                        Block current = world.getBlockAt(x, y, z);

                        if (current.getType() == Material.SIGN_POST) {
                            if (((CraftSign) current.getState()).getLine(0).equalsIgnoreCase("builder")) {

                                current.setType(Material.AIR);
                                termites.add(new BuilderTermite(x, y, z, worldData));
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    private void destroyTermites() {
        for (Termite termite : termites) {
            termite.destroy();
        }
    }

    public static String getNextNPCID() throws Exception {
        if (npcCount >= NPC_MAX) {
            throw new Exception("Too many NPC's!");
        }

        npcID++;
        npcCount++;


        return "" + npcID;
    }

    public static void releaseNPC() {
        npcCount--;
    }
}
