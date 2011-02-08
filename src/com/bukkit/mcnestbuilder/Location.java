/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder;

/**
 *
 * @author covertcj
 */
public class Location {
    public int x,y,z;

    public Location(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.x;
        result = prime * result + this.y;
        result = prime * result + this.z;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        Location other = (Location) obj;
        if (this.x != other.x)
            return false;
        if (this.y != other.y)
            return false;
        if (this.z != other.z)
            return false;

        return true;
    }

    public String toString() {
        return this.x + " " + this.y + " " + this.z;
    }
}
