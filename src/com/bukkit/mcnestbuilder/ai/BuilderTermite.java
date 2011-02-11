/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder.ai;

import com.bukkit.mcnestbuilder.Location;
import com.bukkit.mcnestbuilder.Mediator;
import com.bukkit.mcnestbuilder.WorldData;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.npcspawner.BasicHumanNpc;
import org.bukkit.npcspawner.BasicHumanNpcList;
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

    final int movement_spaces = 5;
    final double wander_weight = 1;
    final double place_probability = 0.5;

    final double min_block_pheromone = 0.1;
    final double max_block_pheromone = 0.4;

    final double lay_rate = 1;

    final Material block_material = Material.GOLD_BLOCK;

    public BuilderTermite(int x, int y, int z, WorldData world, BasicHumanNpcList npcs) {
        this.xO = x;
        this.yO = y;
        this.zO = z;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;

        String key = Mediator.getNextNPCID();
        npc = NpcSpawner.SpawnBasicHumanNpc(key, "BuilderTermite", world.getWorld(), x, y, z, 0, 0);
        npcs.put(key, npc);
    }

    public void act(int timeStep) {
        if (timeStep > Mediator.TIME_STEPS_TRAIL) {

            move();
            placeBlock();
        }
    }

    private void placeBlock() {
        PheromoneLevel pl = this.world.getBlockPheromones(this.x, this.y, this.z);

        if ((pl.queenPheromone > min_block_pheromone && pl.queenPheromone < max_block_pheromone) ||
            (pl.trailPheromone > min_block_pheromone && pl.trailPheromone < max_block_pheromone)) {

            if (Math.random() < place_probability) {
                this.world.setBlockType(x, y, z, getMaterial());
                this.world.getBlockPheromones(x, y, z).cementPheromone += lay_rate;

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
            m == Material.LAVA ||
            m == Material.LEAVES ||
            m == Material.SUGAR_CANE_BLOCK) {
            return false;
        }

        return true;
    }

    private void move() {
        for (int i = 0; i < movement_spaces; i++) {
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
                weights[index] = wander_weight + pl.cementPheromone;
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
