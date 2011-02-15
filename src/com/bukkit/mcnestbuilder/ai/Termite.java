/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder.ai;

/**
 *
 * @author covertcj
 */
public interface Termite {
        public void act(int timeStep);
        
	public void layPheromone(int timeStep);
}
