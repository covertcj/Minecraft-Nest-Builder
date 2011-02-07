package com.bukkit.mcnestbuilder.backend;

import java.util.ArrayList;

public class Location {
	public int x, y, z;

	public Location(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public ArrayList<Location> getNeighbors(int xlength,int ylength,int zlength){
		ArrayList<Location> neighbors = new ArrayList<Location>();
		if(this.x!=0){
			neighbors.add(new Location(x-1,y,z));
		}
		if(this.x!=xlength-1){
			neighbors.add(new Location(x+1,y,z));
		}
		if(this.y!=0){
			neighbors.add(new Location(x,y-1,z));
		}
		if(this.y!=ylength-1){
			neighbors.add(new Location(x,y+1,z));
		}
		if(this.z!=0){
			neighbors.add(new Location(x,y,z-1));
		}
		if(this.z!=zlength-1){
			neighbors.add(new Location(x,y,z+1));
		}
		return neighbors;
	}
}
