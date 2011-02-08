/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder.ai;

import com.bukkit.mcnestbuilder.Mediator;
import com.bukkit.mcnestbuilder.WorldData;
import org.bukkit.entity.Player;
import org.bukkit.npcspawner.BasicHumanNpc;
import org.bukkit.npcspawner.NpcSpawner;

/**
 *
 * @author covertcj
 */
public class BuilderTermite implements Termite {

    BasicHumanNpc npc;
    WorldData world;

    double x, y, z;

    public BuilderTermite(int x, int y, int z, WorldData world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;

        npc = NpcSpawner.SpawnBasicHumanNpc(Mediator.getNextNPCID(), "BuilderTermite", world.getWorld(), x, y, z, 0, 0);
    }

    public void act() {
        x = x + 0.1;
        npc.moveTo(x, y, z, 0, 0);
    }

    public void layPheromone() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void destroy() {
        // TODO: Find a way to destroy the termites
//        ((Player)(npc.getBukkitEntity())).kickPlayer(npc.getName());// .setHealth(0);
        NpcSpawner.RemoveBasicHumanNpc(npc);
        Mediator.releaseNPC();
    }

}
