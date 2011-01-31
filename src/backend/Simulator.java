package backend;

import java.util.ArrayList;

public class Simulator {
	Block[][][] map = new Block[100][100][100];

	// parameters
	double a; // diffusion rate 0<a<1/6
	double v; // Evaporation rate
	double p; // placement probability
	int m; // amount each termite move each time step
	int numBuilderTermites; //numver of builder termites
	int numTimeSteps; // number of time steps to do

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
			//move and do other actios
			for (Termite t : termites) {
				t.move();
			}
			//lay pheromones
			for (Termite t : termites) {
				t.layPheromone();
			}
			//TODO: diffuse pheromone
			//TODO: evaporate pheromones
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
