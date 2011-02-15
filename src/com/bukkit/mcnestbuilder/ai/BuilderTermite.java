/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder.ai;

import com.bukkit.mcnestbuilder.Location;
import com.bukkit.mcnestbuilder.Mediator;
import com.bukkit.mcnestbuilder.Settings;
import com.bukkit.mcnestbuilder.TermiteDestructor;
import com.bukkit.mcnestbuilder.WorldData;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.npcspawner.BasicHumanNpc;
import org.bukkit.npcspawner.NpcSpawner;

/**
 *
 * @author covertcj
 */
public class BuilderTermite implements Termite {

    BasicHumanNpc npc;
    WorldData world;

    int xO, yO, zO;

    int x, y, z;

    final Material block_material = Material.GOLD_BLOCK;

    public BuilderTermite(int x, int y, int z, WorldData world) {
        this.xO = x;
        this.yO = y;
        this.zO = z;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;

        String key = Mediator.getNextNPCID();
        npc = NpcSpawner.SpawnBasicHumanNpc(key, "BuilderTermite", world.getWorld(), x, y, z, 0, 0);

        synchronized(TermiteDestructor.npcLock) {
            TermiteDestructor.npcs.put(key, npc);
        }
    }

    public void act(int timeStep) {
        if (timeStep > Settings.TIMESTEPS_BUILDER_START) {

            move();
            placeBlock();
        }
    }

    private void placeBlock() {
        PheromoneLevel pl = this.world.getBlockPheromones(this.x, this.y, this.z);

        if ((pl.queenPheromone > Settings.BUILDER_MIN_PLACE_PHEROMONE && pl.queenPheromone < Settings.BUILDER_MAX_PLACE_PHEROMONE) ^
            (pl.trailPheromone > Settings.BUILDER_MIN_PLACE_PHEROMONE && pl.trailPheromone < Settings.BUILDER_MAX_PLACE_PHEROMONE)) {

            if (Math.random() < Settings.BUILDER_PLACE_PROBABILITY) {
                try {
                    this.world.setBlockType(x, y, z, getMaterial());
                } catch (NullPointerException ex) {
                    System.out.println("=====================\nWTF, STUPID ERROR?!?!?!\n=====================");
                    System.out.println("Mat: " + this.world.getBlockType(x, y, z));
                    System.out.println("(" + x + ", " + y + ", " + z + ")");
                    System.out.println("getMat(): " + getMaterial());
                    System.out.println("=====================\n=====================\n=====================\n=====================\n=====================");
                }

                this.world.getBlockPheromones(x, y, z).cementPheromone += Settings.BUILDER_LAY_RATE;

                this.x = this.xO;
                this.y = this.yO;
                this.z = this.zO;

                if (npc != null) {
                    npc.moveTo(this.x, this.x, this.x, 0, 0);
                }
            }
        }
    }

    private Material getMaterial() {
        for (int xoff = -1; xoff <= 1; xoff++) {
            for (int yoff = -1; yoff <= 1; yoff++) {
                for (int zoff = -1; zoff <= 1; zoff++) {
                    Material m = world.getBlockType(this.x + xoff, this.y + yoff, this.z + zoff);
                    if (isMaterialSolid(m)) {
                        return m;
                    }
                }
            }
        }

        return this.block_material;
    }

    private boolean isMaterialSolid(Material m) {
        if (m == Material.AIR ||
            m == Material.WATER ||
            m == Material.STATIONARY_WATER ||
            m == Material.LAVA ||
            m == Material.STATIONARY_LAVA ||
            m == Material.LEAVES ||
            m == Material.SUGAR_CANE_BLOCK ||
            m == Material.SAND ||
            m == Material.SIGN ||
            m == Material.GRAVEL ||
            m == Material.TORCH) {

            return false;
        }

        return true;
    }

    private void move() {
        for (int i = 0; i < Settings.BUILDER_MOVE_SPACES; i++) {
            ArrayList<Location> locs = generatePossibleMoves(this.x, this.y, this.z);
            Location loc = getMoveLocation(locs);

            if (loc == null) {
                this.x = this.xO;
                this.y = this.yO;
                this.z = this.zO;
            }
            else {
                this.x = loc.x;
                this.y = loc.y;
                this.z = loc.z;
            }
        }

        if (npc != null) {
            npc.moveTo(this.x, this.y, this.z, 0, 0);
        }
    }

    private Location getMoveLocation(ArrayList<Location> locs) {

        double remaining = Math.random();
        double weights[] = new double[locs.size()];
        double total = 0;
        int index = 0;

        // find weights
        for (int i = 0; i < locs.size(); i++) {
            Location loc = locs.get(i);
            PheromoneLevel pl = this.world.getBlockPheromones(loc.x, loc.y, loc.z);

            if (pl == null) {
                weights[index] = 0;
            }
            else {
                weights[index] = Settings.BUILDER_WANDER_WEIGHT + pl.cementPheromone;
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
        // do nothing
    }

    public void destroy() {
        if (npc != null) {
            NpcSpawner.RemoveBasicHumanNpc(npc);
            Mediator.releaseNPC();
        }
    }

}
