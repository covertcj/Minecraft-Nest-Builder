/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder;

import com.bukkit.mcnestbuilder.ai.PheromoneLevel;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.World;

/**
 *
 * @author covertcj
 */
public class WorldData {
    
    private World world;

    private HashMap<Location, PheromoneLevel> pheromoneLevels;

    private int dimension;
    
    public WorldData(World world, int dimension, Location playerLoc) {
        this.world = world;

        this.pheromoneLevels = new HashMap<Location, PheromoneLevel>();

        int xoff = playerLoc.x - dimension / 2;
        int zoff = playerLoc.z - dimension / 2;

        int yMin = playerLoc.y - Settings.BOUNDARY_DOWN_MAX;
        int yMax = playerLoc.y + Settings.BOUNDARY_UP_MAX;

        for (int x = xoff; x < dimension + xoff; x++) {
            for (int y = yMin; y < yMax; y++) {
                for (int z = zoff; z < dimension + zoff; z++) {
                    this.pheromoneLevels.put(new Location(x, y, z), new PheromoneLevel());
               }
            }
        }
        
        this.dimension = dimension;
    }

    /**
     * Retrieves the material of a block a the given coordinates.
     *
     * @param x The x position of the block
     * @param y The y position of the block
     * @param z The z position of the block
     *
     * @return The block material
     */
    public Material getBlockType(int x, int y, int z) {
        return world.getBlockAt(x, y, z).getType();
    }

    /**
     * Sets the block at a given position to a given material.
     *
     * @param x The x position of the block
     * @param y The y position of the block
     * @param z The z position of the block
     * @param mat The material to set the block to
     */
    public void setBlockType(int x, int y, int z, Material mat) {
        //world.getBlockAt(x, y, z).setType(mat);
        synchronized(TermiteManager.blocksToSpawnLock) {
            TermiteManager.blocksToSpawn.add(new BlockMemory(new Location(x, y, z), mat));
        }
    }

    /**
     * Retrieves a reference to the pheromone data structure at the given block
     * coordinates.
     *
     * @param x The x position of the block
     * @param y The y position of the block
     * @param z The z position of the block
     *
     * @return The pheromones data structure
     */
    public PheromoneLevel getBlockPheromones(int x, int y, int z) {
        Location loc = new Location(x, y, z);
        PheromoneLevel pl = null;

        if (pheromoneLevels.containsKey(loc)) {
            pl = pheromoneLevels.get(loc);
        }

        return pl;
    }

    /**
     * Retrieves the actual bukkit world used by the WorldData structure. This
     * should NOT be used in code, as it will break synchronization. It is
     * simply used to initialize the termite NPC's.
     *
     * @return The representation of the server world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Diffuses the pheromones for each block onto adjacent blocks.
     */
    public void diffusePheromones(int timestep) {
        if (timestep > Settings.TIMESTEPS_BUILDER_START) {
            return;
        }

        HashMap<Location, PheromoneLevel> newPheromoneLevels = new HashMap<Location, PheromoneLevel>();

        for (Entry<Location,PheromoneLevel> entries : pheromoneLevels.entrySet()) {
            Location loc = entries.getKey();
            PheromoneLevel pl = entries.getValue();

            diffusePheromone(loc, pl, newPheromoneLevels);
        }

        pheromoneLevels = newPheromoneLevels;
    }

    private void diffusePheromone(Location loc, PheromoneLevel pl, HashMap<Location, PheromoneLevel> newPheromoneLevels) {
        
        // enter this location into the map
        PheromoneLevel newLevel = new PheromoneLevel(pl);
        newPheromoneLevels.put(loc, newLevel);

        updatePheromone(pl, getBlockPheromones(loc.x + 1, loc.y,     loc.z),     newLevel);
        updatePheromone(pl, getBlockPheromones(loc.x - 1, loc.y,     loc.z),     newLevel);
        updatePheromone(pl, getBlockPheromones(loc.x,     loc.y + 1, loc.z),     newLevel);
        updatePheromone(pl, getBlockPheromones(loc.x,     loc.y - 1, loc.z),     newLevel);
        updatePheromone(pl, getBlockPheromones(loc.x,     loc.y,     loc.z + 1), newLevel);
        updatePheromone(pl, getBlockPheromones(loc.x,     loc.y,     loc.z - 1), newLevel);

    }

    private void updatePheromone(PheromoneLevel updatee, PheromoneLevel updater, PheromoneLevel newLevel) {

        if (updater == null) {
            return;
        }

        newLevel.cementPheromone += (updater.cementPheromone - updatee.cementPheromone) * Settings.PHEROMONE_DIFFUSE_RATE;
        newLevel.queenPheromone  += (updater.queenPheromone  - updatee.queenPheromone)  * Settings.PHEROMONE_DIFFUSE_RATE;
        newLevel.trailPheromone  += (updater.trailPheromone  - updatee.trailPheromone)  * Settings.PHEROMONE_DIFFUSE_RATE_TRAIL;
        
    }

    /**
     * Partially evaporates all pheromones in the world.
     */
    public void evaporatePheromones(int timestep) {
        if (timestep > Settings.TIMESTEPS_BUILDER_START) {
            return;
        }
        
        for (PheromoneLevel pl : pheromoneLevels.values()) {
            pl.cementPheromone = pl.cementPheromone * Settings.PHEROMONE_EVAPORATION_RATE;
            pl.queenPheromone  = pl.queenPheromone  * Settings.PHEROMONE_EVAPORATION_RATE;
            pl.trailPheromone  = pl.trailPheromone  * Settings.PHEROMONE_EVAPORATION_RATE_TRAIL;
        }
    }
}
