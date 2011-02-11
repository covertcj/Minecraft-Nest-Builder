/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder;

import org.bukkit.npcspawner.BasicHumanNpc;
import org.bukkit.npcspawner.BasicHumanNpcList;
import org.bukkit.npcspawner.NpcSpawner;

/**
 *
 * @author covertcj
 */
public class TermiteDestructor implements Runnable {

    public static BasicHumanNpcList npcs = new BasicHumanNpcList();
    public static final Object npcLock = new Object();

    public void run() {
        synchronized(Mediator.runningLock) {
            if (Mediator.running) {
                return;
            }
        }
        
        synchronized(npcLock) {
            for (BasicHumanNpc npc : npcs.values()) {
                NpcSpawner.RemoveBasicHumanNpc(npc);
            }

            npcs.clear();
        }
    }

}
