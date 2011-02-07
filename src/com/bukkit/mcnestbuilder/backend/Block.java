package com.bukkit.mcnestbuilder.backend;
public class Block {
	public double queenPheromone;
	public double trailPheromone;
	public double cementPheromone;
	public boolean isMaterial;
	
	public Block(){
		queenPheromone = 0;
		trailPheromone = 0;
		cementPheromone = 0;
		isMaterial = false;
	}
	
	public Block(double c){
		cementPheromone = c;
		isMaterial = true;
	}
}
