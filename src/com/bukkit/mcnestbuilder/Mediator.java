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

    public static final int TIME_STEPS_TRAIL = 500;
    public static final int TIME_STEPS_BUILDER = 1250 + TIME_STEPS_TRAIL;
    public static final int BUILDERS = 100;

    Player caller;
    World world;
    WorldData worldData;

    ArrayList<QueenTermite> queenTermites;
    ArrayList<BuilderTermite> builderTermites;
    ArrayList<TrailTermite> trailTermites;
    ArrayList<Termite> termites;

    int dimension;
    int timestep;
    int duration;

    public static final int NPC_MAX = 500;
    public static int npcID;
    public static int npcCount;
    public static final int Y_OFFSET_DOWN = 25;
    public static final int Y_OFFSET_UP   = 35;

    public static boolean running = false;
    public static final Object runningLock = new Object();

    public Mediator(Player player, int dimension, int duration) {

        this.dimension = dimension;
        this.duration = duration;
        this.timestep = (duration * 1000) / (TIME_STEPS_BUILDER - TIME_STEPS_TRAIL);

        this.caller = player;

        int x = this.caller.getLocation().getBlockX();
        int y = this.caller.getLocation().getBlockY();
        int z = this.caller.getLocation().getBlockZ();

        this.world = this.caller.getWorld();
        this.worldData = new WorldData(this.world, this.dimension, new Location(x, y, z));

        this.caller.sendMessage("Initializing Nest Builder at (" + x + ", " + y + ", " + z + ") with a dimension of " + dimension + " over " + duration + " seconds...");

        queenTermites = new ArrayList<QueenTermite>();
        builderTermites = new ArrayList<BuilderTermite>();
        trailTermites = new ArrayList<TrailTermite>();
        termites = new ArrayList<Termite>();
    }

    /**
     * Runs the Nest Builder. Should be used as a separate thread.
     */
    public void run() {

        synchronized(runningLock) {
            running = true;
        }

        boolean done = false;
        int currentStep = 0;

        long startTime = 0;
        long endTime = 0;
        long timeToSleep = 0;

        // TODO: Run the bot
        while (!done) {
            startTime = System.currentTimeMillis();

            for (Termite termite : termites) {
                termite.act(currentStep);
            }

            for (Termite termite : termites) {
                termite.layPheromone(currentStep);
            }

            worldData.diffusePheromones();

            worldData.evaporatePheromones();

            if (currentStep > TIME_STEPS_TRAIL) {
                endTime = System.currentTimeMillis();
                timeToSleep = this.timestep - (endTime - startTime);

                try {
                    if (timeToSleep > 0) {
                        Thread.sleep(timeToSleep);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Mediator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            // temporary limit to how long it runs
            currentStep++;
            if (currentStep >= TIME_STEPS_BUILDER) {
                done = true;
            }
        }

        // cleanup
        this.caller.sendMessage("Nest Building process has completed.");

        synchronized(runningLock) {
            running = false;
        }
    }

    public boolean InitializeTermtites() {
        int playerX = this.caller.getLocation().getBlockX();
        int playerY = this.caller.getLocation().getBlockY();
        int playerZ = this.caller.getLocation().getBlockZ();

        int xOrigin = playerX - dimension / 2;
        int zOrigin = playerZ - dimension / 2;

        ArrayList<Location> queenLocs = new ArrayList<Location>();
        ArrayList<Location> builderLocs = new ArrayList<Location>();
        ArrayList<Location> trailLocs = new ArrayList<Location>();

        // find the main queen location
        //queenLocs.add(new Location(queenX, queenY, queenZ));

        int yMin = playerY - Y_OFFSET_DOWN;
        int yMax = playerY + Y_OFFSET_UP;

        // find the locations of other termites
        for (int x = xOrigin; x < dimension + xOrigin; x++) {
            for (int z = zOrigin; z < dimension + zOrigin; z++) {
                for (int y = yMin; y < yMax; y++) {

                    Block current = world.getBlockAt(x, y, z);

                    // if the current block is a sign, we might have found a location for a termite
                    if (current.getType() == Material.SIGN_POST) {
                        // check if it is a queen sign
                        if (((CraftSign) current.getState()).getLine(0).equalsIgnoreCase("queen")) {

                            // add a builder location
                            queenLocs.add(new Location(x, y, z));
                        }

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
            queenTermites.add(new QueenTermite(loc.x, loc.y, loc.z, worldData));
        }

        // add in the builder termites
//        for (Location loc : builderLocs) {
//            termites.add(new BuilderTermite(loc.x, loc.y, loc.z, worldData));
//        }
        for (int i = 0; i < BUILDERS; i++) {
            Location loc = builderLocs.get(i % builderLocs.size());
            builderTermites.add(new BuilderTermite(loc.x, loc.y, loc.z, worldData));
        }

        // add in the trail termites
        for (Location loc : trailLocs) {
            trailTermites.add(new TrailTermite(loc.x, loc.y, loc.z, worldData));
        }

        termites.addAll(queenTermites);
        termites.addAll(builderTermites);
        termites.addAll(trailTermites);

        return true;
    }

    private void destroyTermites() {
        for (Termite termite : queenTermites) {
            termite.destroy();
        }

        for (Termite termite : builderTermites) {
            termite.destroy();
        }
        
        for (Termite termite : trailTermites) {
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
