package com.bukkit.mcnestbuilder.backend;

import java.util.ArrayList;

public class BuilderTermite implements Termite {
	public Location loc;

	public BuilderTermite(Location l) {
		this.loc = l;
	}
	
	@Override
	public void act(Block[][][] map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void layPheromone() {

	}
	
	public void move(Block[][][] map) {
		//TODO discuss the pheromone following characteristics of the builderTermite
	}
	
	public ArrayList<Location> getValidNeighbors(Block[][][] map){
		ArrayList<Location> neighbors = new ArrayList<Location>();
		for(int i=-1;i<=1;i++){
			if( (loc.x+i<0) || (loc.x+i>map.length))
				continue;
			for(int j=-1;j<=1;j++){
				if( (loc.y+j<0) || (loc.y+j>map[0].length))
					continue;
				for(int k=-1;k<=1;k++){
					if( (loc.z+k<0) || (loc.z+k>map[0][0].length))
						continue;
					if(i==0 && j==0 && k==0)
						continue;
					if(map[loc.x+i][loc.y+j][loc.z+k].isMaterial)
						continue;
					if(isGrounded(loc.x+i, loc.y+j, loc.z+k, map))
						neighbors.add(new Location(loc.x+i, loc.y+j, loc.z+k));
				}
			}
		}
		return null;
	}
	
	public boolean isGrounded(int x, int y, int z, Block[][][] map){
		for(int i=-1;i<=1;i++)
			for(int j=-1;j<=1;j++)
				for(int k=-1;k<=1;k++)
					if(map[x+i][y+j][z+k].isMaterial)
						return true;
		return false;
	}
}
