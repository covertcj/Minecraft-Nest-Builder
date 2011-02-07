package com.bukkit.mcnestbuilder.backend;

import java.util.ArrayList;

public class Simulator {
	Block[][][] map;

	// parameters
	double a; // diffusion rate 0<a<1/6
	double v; // Evaporation rate
	double p; // placement probability
	int m; // amount each termite move each time step
	int numBuilderTermites; // number of builder termites
	int numTimeSteps; // number of time steps to do

	int xlength, ylength, zlength;

	ArrayList<Termite> termites;

	public Simulator() {
		// spawn intial builder termites
		// TODO: make them spawn at correct locations
		for (int i = 0; i < numBuilderTermites; i++) {
			termites.add(new BuilderTermite(new Location(0, 0, 0)));
		}

	}

	public void run() {
		for (int i = 0; i < numTimeSteps; i++) {
			// move and do other actions
			for (Termite t : termites) {
				t.act(map);
			}
			
			// lay pheromones
			for (Termite t : termites) {
				t.layPheromone();
			}

			// diffuse pheromone
			this.diffusePheromones();

			// evaporate pheromones
			this.evaporatePheromones();

		}
	}

	private void diffusePheromones() {
		Block[][][] newmap = new Block[this.xlength][this.ylength][this.zlength];
		// set newmap equal to old map
		for (int i = 0; i < xlength; i++) {
			for (int j = 0; j < ylength; j++) {
				for (int k = 0; k < zlength; k++) {
					newmap[i][j][k] = this.map[i][j][k];
				}
			}
		}

		// for each block diffuse the pheromone
		for (int i = 0; i < xlength; i++) {
			for (int j = 0; j < ylength; j++) {
				for (int k = 0; k < zlength; k++) {
					diffusePheromone(i, j, k, newmap);
				}
			}
		}
		this.map = newmap;
	}

	private void diffusePheromone(int x, int y, int z, Block[][][] newmap) {
		if (x != 0) {
			newmap[x][y][z].cementPheromone += (this.map[x - 1][y][z].cementPheromone - this.map[x][y][z].cementPheromone)
					* this.a;
			newmap[x][y][z].queenPheromone += (this.map[x - 1][y][z].queenPheromone - this.map[x][y][z].queenPheromone)
					* this.a;
			newmap[x][y][z].trailPheromone += (this.map[x - 1][y][z].trailPheromone - this.map[x][y][z].trailPheromone)
					* this.a;
		}
		if (x != xlength - 1) {
			newmap[x][y][z].cementPheromone += (this.map[x + 1][y][z].cementPheromone - this.map[x][y][z].cementPheromone)
					* this.a;
			newmap[x][y][z].queenPheromone += (this.map[x + 1][y][z].queenPheromone - this.map[x][y][z].queenPheromone)
					* this.a;
			newmap[x][y][z].trailPheromone += (this.map[x + 1][y][z].trailPheromone - this.map[x][y][z].trailPheromone)
					* this.a;
		}
		if (y != 0) {
			newmap[x][y][z].cementPheromone += (this.map[x][y - 1][z].cementPheromone - this.map[x][y][z].cementPheromone)
					* this.a;
			newmap[x][y][z].queenPheromone += (this.map[x][y - 1][z].queenPheromone - this.map[x][y][z].queenPheromone)
					* this.a;
			newmap[x][y][z].trailPheromone += (this.map[x][y - 1][z].trailPheromone - this.map[x][y][z].trailPheromone)
					* this.a;

		}
		if (y != ylength - 1) {
			newmap[x][y][z].cementPheromone += (this.map[x][y + 1][z].cementPheromone - this.map[x][y][z].cementPheromone)
					* this.a;
			newmap[x][y][z].queenPheromone += (this.map[x][y + 1][z].queenPheromone - this.map[x][y][z].queenPheromone)
					* this.a;
			newmap[x][y][z].trailPheromone += (this.map[x][y + 1][z].trailPheromone - this.map[x][y][z].trailPheromone)
					* this.a;
		}
		if (z != 0) {
			newmap[x][y][z].cementPheromone += (this.map[x][y][z - 1].cementPheromone - this.map[x][y][z].cementPheromone)
					* this.a;
			newmap[x][y][z].queenPheromone += (this.map[x][y][z - 1].queenPheromone - this.map[x][y][z].queenPheromone)
					* this.a;
			newmap[x][y][z].trailPheromone += (this.map[x][y][z - 1].trailPheromone - this.map[x][y][z].trailPheromone)
					* this.a;
		}
		if (z != zlength - 1) {
			newmap[x][y][z].cementPheromone += (this.map[x][y][z + 1].cementPheromone - this.map[x][y][z].cementPheromone)
					* this.a;
			newmap[x][y][z].queenPheromone += (this.map[x][y][z + 1].queenPheromone - this.map[x][y][z].queenPheromone)
					* this.a;
			newmap[x][y][z].trailPheromone += (this.map[x][y][z + 1].trailPheromone - this.map[x][y][z].trailPheromone)
					* this.a;
		}
	}

	private void evaporatePheromones() {
		for (int i = 0; i < xlength; i++) {
			for (int j = 0; j < ylength; j++) {
				for (int k = 0; k < zlength; k++) {
					this.map[i][j][k].cementPheromone = v
							* this.map[i][j][k].cementPheromone;
					this.map[i][j][k].queenPheromone = v
							* this.map[i][j][k].queenPheromone;
					this.map[i][j][k].trailPheromone = v
							* this.map[i][j][k].trailPheromone;
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
