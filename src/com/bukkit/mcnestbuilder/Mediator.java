/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder;

import com.bukkit.mcnestbuilder.ai.BuilderTermite;
import com.bukkit.mcnestbuilder.ai.QueenTermite;
import com.bukkit.mcnestbuilder.ai.Termite;
import com.bukkit.mcnestbuilder.ai.TrailTermite;
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

    final int TIME_STEPS = 1000;

    Player caller;
    World world;
    WorldData worldData;

    ArrayList<Termite> termites;

    int dimension;
    int timestep;
    int duration;

    static final int NPC_MAX = 500;
    static int npcID;
    static int npcCount;

    public Mediator(Player player, int dimension, int duration) {

        this.caller = player;
        this.world = this.caller.getWorld();
        this.worldData = new WorldData(this.world);

        this.dimension = dimension;
        this.duration = duration;
        this.timestep = (duration * 1000) / TIME_STEPS;

        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();

        this.caller.sendMessage("Initializing Nest Builder at (" + x + ", " + y + ", " + z + ") with a dimension of " + dimension + " over " + duration + " seconds...");

        termites = new ArrayList<Termite>();
    }

    /**
     * Runs the Nest Builder. Should be used as a separate thread.
     */
    public void run() {

        boolean done = false;
        int count = 0;

        // TODO: Run the bot
        while (!done) {

            // process termite actions
            for (Termite termite : termites) {
                termite.act();
            }

            // update pheromones based on termite positions
            for (Termite termite : termites) {
                termite.layPheromone();
            }

            // TODO: diffuse pheromones

            // TODO: diminish pheromones
            
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

    public boolean InitializeTermtites() {
        int queenX = this.caller.getLocation().getBlockX();
        int queenY = this.caller.getLocation().getBlockY();
        int queenZ = this.caller.getLocation().getBlockZ();

        int xOrigin = queenX - dimension / 2;
        int zOrigin = queenZ - dimension / 2;

        ArrayList<Location> queenLocs = new ArrayList<Location>();
        ArrayList<Location> builderLocs = new ArrayList<Location>();
        ArrayList<Location> trailLocs = new ArrayList<Location>();

        // find the main queen location
        queenLocs.add(new Location(queenX, queenY, queenZ));

        // find the locations of other termites
        for (int x = xOrigin; x < dimension + xOrigin; x++) {
            for (int z = zOrigin; z < dimension + zOrigin; z++) {
                for (int y = 0; y < 128; y++) {

                    Block current = world.getBlockAt(x, y, z);

                    // if the current block is a sign, we might have found a location for a termite
                    if (current.getType() == Material.SIGN_POST) {
                        // check if it is a builder sign
                        if (((CraftSign) current.getState()).getLine(0).equalsIgnoreCase("builder")) {

                            // add a builder location
                            builderLocs.add(new Location(x, y, z));
                        }
                    }
                }
            }
        }

        // check to make sure our server can handle the NPC's
        int numNPCs = queenLocs.size() + builderLocs.size() + trailLocs.size();
        if (numNPCs + npcCount > NPC_MAX) {
            return false;
        }

        // add in the queen termites
        for (Location loc : queenLocs) {
            termites.add(new QueenTermite(loc.x, loc.y, loc.z, worldData));
        }

        // add in the builder termites
        for (Location loc : builderLocs) {
            termites.add(new BuilderTermite(loc.x, loc.y, loc.z, worldData));
        }

        // add in the trail termites
        for (Location loc : trailLocs) {
            termites.add(new TrailTermite(loc.x, loc.y, loc.z, worldData));
        } 

        return true;
    }

    private void destroyTermites() {
        for (Termite termite : termites) {
            termite.destroy();
        }
    }

    public static String getNextNPCID() {
        npcID++;
        npcCount++;

        return "" + npcID;
    }

    public static void releaseNPC() {
        npcCount--;
    }
}
