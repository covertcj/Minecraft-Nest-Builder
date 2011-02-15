/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder;

import com.bukkit.mcnestbuilder.ai.BuilderTermite;
import java.util.ArrayList;
import javax.print.attribute.standard.Media;
import org.bukkit.Material;
import org.bukkit.npcspawner.BasicHumanNpc;
import org.bukkit.npcspawner.BasicHumanNpcList;
import org.bukkit.npcspawner.NpcSpawner;

/**
 *
 * @author covertcj
 */
public class TermiteManager implements Runnable {

    public static BasicHumanNpcList trailNpcs = new BasicHumanNpcList();
    public static final Object trailNpcLock = new Object();

    public static BasicHumanNpcList builderNpcs = new BasicHumanNpcList();
    public static final Object builderNpcLock = new Object();

    public static ArrayList<BuilderTermite> buildersToSpawn = new ArrayList<BuilderTermite>();
    public static final Object buildersToSpawnLock = new Object();

    public static ArrayList<BlockMemory> blocksToSpawn = new ArrayList<BlockMemory>();
    public static final Object blocksToSpawnLock = new Object();

    public void run() {

        spawnBlocks();
        spawnBuilders();
        destroyBuilders();
        destroyTrails();
    }

    private void spawnBlocks() {
        synchronized(blocksToSpawnLock) {
            for (BlockMemory mem : blocksToSpawn) {
                Mediator.world.getBlockAt(mem.location.x, mem.location.y, mem.location.z).setType(mem.material);
            }

            blocksToSpawn.clear();
        }
    }

    private void spawnBuilders() {
        synchronized(buildersToSpawnLock) {
            for (BuilderTermite builder : buildersToSpawn) {
                builder.spawnNpc();
            }

            buildersToSpawn.clear();
        }
    }

    private void destroyTrails() {
        synchronized(Mediator.runningTrailsLock) {
            if (Mediator.runningTrails) {
                return;
            }
        }

        synchronized(trailNpcLock) {
            for (BasicHumanNpc npc : trailNpcs.values()) {
                NpcSpawner.RemoveBasicHumanNpc(npc);
            }

            trailNpcs.clear();
        }
    }

    private void destroyBuilders() {
        synchronized(Mediator.runningBuildersLock) {
            if (Mediator.runningBuilders) {
                return;
            }
        }

        synchronized(builderNpcLock) {
            for (BasicHumanNpc npc : builderNpcs.values()) {
                NpcSpawner.RemoveBasicHumanNpc(npc);
            }

            builderNpcs.clear();
        }
    }
}
