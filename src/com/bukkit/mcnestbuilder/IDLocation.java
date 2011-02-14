/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder;

/**
 *
 * @author covertcj
 */
public class IDLocation extends Location {

    public int id;

    public IDLocation(int x, int y, int z, int id) {
        super(x, y, z);
        
        this.id = id;
    }
}
