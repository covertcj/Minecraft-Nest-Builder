/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder.ai;

import com.bukkit.mcnestbuilder.Location;
import com.bukkit.mcnestbuilder.Mediator;
import com.bukkit.mcnestbuilder.IDLocation;
import com.bukkit.mcnestbuilder.Settings;
import com.bukkit.mcnestbuilder.TermiteManager;
import com.bukkit.mcnestbuilder.WorldData;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.npcspawner.NpcSpawner;
import org.bukkit.npcspawner.BasicHumanNpc;

/**
 *
 * @author covertcj
 */
public class TrailTermite implements Termite {
    
    private BasicHumanNpc npc;
    private WorldData world;

    private IDLocation queenLocation;
    private Location startLocation;

    private int x, y, z;

    private boolean returning;

    private ArrayList<Location> path;

    public TrailTermite(int x, int y, int z, IDLocation queenLoc, WorldData world) {
        this.startLocation = new Location(x, y, z);
        this.queenLocation = queenLoc;

        this.x = x;
        this.y = y;
        this.z = z;
        
        this.world = world;

        this.returning = false;
        this.path = new ArrayList<Location>();

        String key = Mediator.getNextNPCID();
        npc = NpcSpawner.SpawnBasicHumanNpc(key, "TrailTermite", world.getWorld(), x, y, z, 0, 0);

        synchronized(TermiteManager.trailNpcLock) {
            TermiteManager.trailNpcs.put(key, npc);
        }
    }

    public void act(int timeStep) {

        if (this.returning == false) {
            ArrayList<Location> possibleMoves = generatePossibleMoves(this.x, this.y, this.z);
            Location currentLoc = new Location(this.x, this.y, this.z);
            Location moveLoc = getMoveLocation(possibleMoves);

            this.x = moveLoc.x;
            this.y = moveLoc.y;
            this.z = moveLoc.z;

            if (this.x == queenLocation.x &&
                this.y == queenLocation.y &&
                this.z == queenLocation.z) {

                this.returning = true;
            }

            this.path.add(currentLoc);
        }
        else {
            if (this.path.size() > 0) {
                Location moveLoc = this.path.get(this.path.size()-1);
                this.path.remove(this.path.size()-1);

                this.x = moveLoc.x;
                this.y = moveLoc.y;
                this.z = moveLoc.z;

                if (this.x == startLocation.x &&
                    this.y == startLocation.y &&
                    this.z == startLocation.z) {
                    
                    this.path.clear();
                    this.returning = false;
                }
            }
            else {
                this.path.clear();
                this.returning = false;
            }
        }

        npc.moveTo(x, y, z, 0, 0);
    }

    private Location getMoveLocation(ArrayList<Location> locs) {

        double remaining = Math.random();
        double weights[] = new double[locs.size()];
        double total = 0;
        int index = 0;

        double dist = Math.sqrt(Math.pow(this.queenLocation.x - this.x, 2.0) + Math.pow(this.queenLocation.y - this.y, 2.0) + Math.pow(this.queenLocation.z - this.z, 2.0));

        // find weights
        for (int i = 0; i < locs.size(); i++) {
            Location loc = locs.get(i);
            PheromoneLevel pl = this.world.getBlockPheromones(loc.x, loc.y, loc.z);

            double locdist = (dist - Math.sqrt(Math.pow(this.queenLocation.x - loc.x, 2) + Math.pow(this.queenLocation.y - loc.y, 2) + Math.pow(this.queenLocation.z - loc.z, 2))) * 2;

            if (pl == null) {
                weights[index] = 0;
            }
            else {
                weights[index] = Settings.TRAIL_WANDER_WEIGHT + /*pl.trailPheromone + */locdist;
            }

            total += weights[index];

            index++;
        }

        // normalize and find the location to move to
        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i] / total;

            remaining = remaining - weights[i];

            if (remaining <= 0) {
                return locs.get(i);
            }
        }

        if (locs.isEmpty()) {
            return null;
        }

        return locs.get(locs.size() - 1);
    }

    private ArrayList<Location> generatePossibleMoves(int xpos, int ypos, int zpos) {
        ArrayList<Location> locs = new ArrayList<Location>();

        for (int xoff = -1; xoff <= 1; xoff++) {
            for (int yoff = -1; yoff <= 1; yoff++) {
                for (int zoff = -1; zoff <= 1; zoff++) {
                    if (!(xoff == 0 && yoff == 0 && zoff == 0)) {
                        if (world.getBlockType(xpos + xoff, ypos + yoff, zpos + zoff) == Material.AIR &&
                            isGrounded(xpos + xoff, ypos + yoff, zpos + zoff)) {

                            locs.add(new Location(xpos + xoff, ypos + yoff, zpos + zoff));
                        }
                    }
                }
            }
        }

        return locs;
    }

    private boolean isGrounded(int xpos, int ypos, int zpos) {

        for (int xoff = -1; xoff <= 1; xoff++) {
            for (int yoff = -1; yoff <= 1; yoff++) {
                for (int zoff = -1; zoff <= 1; zoff++) {
                    if (world.getBlockType(xpos + xoff, ypos + yoff, zpos + zoff) != Material.AIR) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void layPheromone(int timeStep) {
        if (returning) {
            PheromoneLevel pl = world.getBlockPheromones(x, y, z);

            if (pl != null) {
                pl.trailPheromone += Settings.TRAIL_LAY_RATE;
            }
        }
    }
}
