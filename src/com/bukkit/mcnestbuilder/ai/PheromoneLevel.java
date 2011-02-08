/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder.ai;

/**
 *
 * @author covertcj
 */
public class PheromoneLevel {
	public double queenPheromone;
	public double trailPheromone;
	public double cementPheromone;

        public PheromoneLevel() {
            this.queenPheromone  = 0;
            this.trailPheromone  = 0;
            this.cementPheromone = 0;
        }

        public PheromoneLevel(PheromoneLevel pl) {
            this.queenPheromone  = pl.queenPheromone;
            this.trailPheromone  = pl.trailPheromone;
            this.cementPheromone = pl.cementPheromone;
        }
}
