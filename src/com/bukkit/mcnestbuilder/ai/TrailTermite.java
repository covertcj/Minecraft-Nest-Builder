/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder.ai;

import com.bukkit.mcnestbuilder.Mediator;
import com.bukkit.mcnestbuilder.WorldData;
import org.bukkit.npcspawner.NpcSpawner;
import org.bukkit.World;
import org.bukkit.npcspawner.BasicHumanNpc;

/**
 *
 * @author covertcj
 */
public class TrailTermite implements Termite {
    
    BasicHumanNpc npc;
    WorldData world;

    double x, y, z;

    public TrailTermite(int x, int y, int z, WorldData world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;

        npc = NpcSpawner.SpawnBasicHumanNpc(Mediator.getNextNPCID(), "QueenTermite", world.getWorld(), x, y, z, 0, 0);
    }

    public void act() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void layPheromone() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void destroy() {
        NpcSpawner.RemoveBasicHumanNpc(npc);
        Mediator.releaseNPC();
    }

}
