/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder;

import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author covertcj
 */
public class Mediator {

    Player caller;
    World world;

    public Mediator() {
        world = null;
    }

    public void run(int x, int y, int z, int dimension) {

        this.caller.sendMessage("Initializing Nest Builder at (" + x + ", " + y + ", " + z + ") with a dimension of " + dimension + "...");

        // TODO: Run the bot

        this.caller.sendMessage("Nest Building process has completed.");

    }

    public void setCaller(Player player) {
        this.caller = player;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
