/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder.ai;

import com.bukkit.mcnestbuilder.Mediator;
import com.bukkit.mcnestbuilder.Settings;
import com.bukkit.mcnestbuilder.TermiteDestructor;
import com.bukkit.mcnestbuilder.WorldData;
import org.bukkit.npcspawner.BasicHumanNpc;
import org.bukkit.npcspawner.NpcSpawner;

/**
 *
 * @author covertcj
 */
public class QueenTermite implements Termite {

    private BasicHumanNpc npc;
    private WorldData world;

    private int x, y, z;

    public QueenTermite(int x, int y, int z, WorldData world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;

        String key = Mediator.getNextNPCID();
        npc = NpcSpawner.SpawnBasicHumanNpc(key, "QueenTermite", world.getWorld(), x, y, z, 0, 0);

        synchronized(TermiteDestructor.npcLock) {
            TermiteDestructor.npcs.put(key, npc);
        }
    }

    public void act(int timeStep) {
        // do nothing
    }

    public void layPheromone(int timeStep) {
//        throw new UnsupportedOperationException("Not supported yet.");
        world.getBlockPheromones(x, y, z).queenPheromone += Settings.QUEEN_LAY_RATE;
    }

    public void destroy() {
        if (npc != null) {
            NpcSpawner.RemoveBasicHumanNpc(npc);
            Mediator.releaseNPC();
        }
    }

}
