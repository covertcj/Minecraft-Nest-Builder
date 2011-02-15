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
    
    public static int npcID;
    public static int npcCount;
    public static final int Y_OFFSET_DOWN = 25;
    public static final int Y_OFFSET_UP   = 35;

    public static boolean running = false;
    public static final Object runningLock = new Object();

    public Mediator(Player player, int dimension, int timestep) {

        this.dimension = dimension;
        this.timestep = timestep;

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

        // run the bot
        while (!done) {
            startTime = System.currentTimeMillis();

            // perform all termite actions first
            for (Termite termite : termites) {
                termite.act(currentStep);
            }

            // lay pheromones after all termite actions
            for (Termite termite : termites) {
                termite.layPheromone(currentStep);
            }

            // update world pheromones
            this.worldData.diffusePheromones(currentStep);
            this.worldData.evaporatePheromones(currentStep);

            // remove termites when they are done
            if (currentStep == Settings.TIMESTEPS_BUILDER_START) {
                ArrayList<Termite> termites2 = new ArrayList<Termite>();

                for (Termite termite : this.termites) {
                    if (termite instanceof BuilderTermite)
                        termites2.add(termite);
                }

                this.termites = termites2;
            }

            // calculate wait time to limit the timestep
            endTime = System.currentTimeMillis();
            timeToSleep = this.timestep - (endTime - startTime);

            // limit the timestep
            try {
                if (timeToSleep > 0) {
                    Thread.sleep(timeToSleep);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Mediator.class.getName()).log(Level.SEVERE, null, ex);
            }

            // end the loop after so many iterations
            currentStep++;
            if (currentStep >= Settings.TIMESTEPS_FINISH) {
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

        ArrayList<IDLocation> queenLocs = new ArrayList<IDLocation>();
        ArrayList<Location> builderLocs = new ArrayList<Location>();
        ArrayList<IDLocation> trailLocs = new ArrayList<IDLocation>();

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
                        CraftSign sign = ((CraftSign) (current.getState()));

                        // check if it is a queen sign
                        if (sign.getLine(0).equalsIgnoreCase("queen")) {

                            // add a builder location
                            queenLocs.add(new IDLocation(x, y, z, Integer.valueOf(((CraftSign)current.getState()).getLine(1))));

                            current.setTypeId(0);
                        }

                        // check if it is a builder sign
                        if (sign.getLine(0).equalsIgnoreCase("builder")) {

                            // add a builder location
                            builderLocs.add(new Location(x, y, z));

                            current.setTypeId(0);
                        }

                        // check if it is a builder sign
                        if (sign.getLine(0).equalsIgnoreCase("trail")) {

                            // add a builder location
                            trailLocs.add(new IDLocation(x, y, z, Integer.valueOf(((CraftSign)current.getState()).getLine(1))));

                            current.setTypeId(0);
                        }
                    }
                }
            }
        }

        // add in the queen termites
        for (Location loc : queenLocs) {
            queenTermites.add(new QueenTermite(loc.x, loc.y, loc.z, worldData));
        }

        // distribute a number of builders to the builder signs
        for (int i = 0; i < Settings.BUILDER_COUNT; i++) {
            Location loc = builderLocs.get(i % builderLocs.size());
            builderTermites.add(new BuilderTermite(loc.x, loc.y, loc.z, worldData));
        }

        // add in the trail termites
        for (IDLocation loc : trailLocs) {
            IDLocation targetLoc = null;

            for (IDLocation queenLoc : queenLocs) {
                if (queenLoc.id == loc.id) {
                    targetLoc = queenLoc;
                    break;
                }
            }

            double dist = Math.sqrt(Math.pow(targetLoc.x - loc.x, 2.0) + Math.pow(targetLoc.y - loc.y, 2.0) + Math.pow(targetLoc.z - loc.z, 2.0));

            for (int i = 0; i < dist; i++) {
                trailTermites.add(new TrailTermite(loc.x, loc.y, loc.z, targetLoc, worldData));
            }
        }

        termites.addAll(queenTermites);
        termites.addAll(builderTermites);
        termites.addAll(trailTermites);

        return true;
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
