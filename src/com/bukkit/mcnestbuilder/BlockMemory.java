/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder;

import org.bukkit.Material;


/**
 *
 * @author covertcj
 */
public class BlockMemory {
    public Location location;
    public Material material;

    public BlockMemory(Location loc, Material mat) {
        this.location = loc;
        this.material = mat;
    }
}
