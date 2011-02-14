/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.mcnestbuilder;

import java.io.File;
import org.bukkit.Material;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author covertcj
 */
public class Settings {
    private static Configuration configuration;

    public static int BUILDER_COUNT = 100;
    public static int BUILDER_MOVE_SPACES = 5;
    public static int BUILDER_WANDER_WEIGHT = 1;
    public static double BUILDER_PLACE_PROBABILITY = 0.5;
    public static double BUILDER_MIN_PLACE_PHEROMONE = 0.1;
    public static double BUILDER_MAX_PLACE_PHEROMONE = 0.5;
    public static double BUILDER_LAY_RATE = 1;
    private static String BUILDER_DEFAULT_BLOCK_STR = "GOLD_BLOCK";
    public static Material BUILDER_DEFAULT_BLOCK = Material.GOLD_BLOCK;

    public static double QUEEN_LAY_RATE = 1000;

    public static double TRAIL_LAY_RATE = 300;
    public static double TRAIL_WANDER_WEIGHT = 1.5;

    public static double PHEROMONE_DIFFUSE_RATE = 0.145;
    public static double PHEROMONE_DIFFUSE_RATE_TRAIL = 0.166;
    public static double PHEROMONE_EVAPORATION_RATE = 0.9;
    public static double PHEROMONE_EVAPORATION_RATE_TRAIL = 0.95;


    public static int BOUNDARY_UP_MAX = 20;
    public static int BOUNDARY_DOWN_MAX = 20;

    public static int TIMESTEPS_BUILDER_START = 500;
    public static int TIMESTEPS_FINISH = 1750;

    public static void Initialize() {
        configuration = new Configuration(new File("plugins/MCNestBuilder/config.yml"));

        BUILDER_COUNT = configuration.getInt("termites.builder.count", BUILDER_COUNT);
        BUILDER_MOVE_SPACES = configuration.getInt("termites.builder.movement_spaces", BUILDER_MOVE_SPACES);
        BUILDER_WANDER_WEIGHT = configuration.getInt("termites.builder.wander_weight", BUILDER_WANDER_WEIGHT);
        BUILDER_MIN_PLACE_PHEROMONE = configuration.getDouble("termites.builder.min_place_pheromone", BUILDER_MIN_PLACE_PHEROMONE);
        BUILDER_MAX_PLACE_PHEROMONE = configuration.getDouble("termites.builder.max_place_pheromone", BUILDER_MAX_PLACE_PHEROMONE);
        BUILDER_LAY_RATE = configuration.getDouble("termites.builder.pheromone_lay_rate", BUILDER_LAY_RATE);
        BUILDER_DEFAULT_BLOCK = Material.matchMaterial(configuration.getString("termites.builder.default_block", BUILDER_DEFAULT_BLOCK_STR));

        QUEEN_LAY_RATE = configuration.getDouble("termites.queen.pheromone_lay_rate", QUEEN_LAY_RATE);

        TRAIL_LAY_RATE = configuration.getDouble("termites.trail.pheromone_lay_rate", TRAIL_LAY_RATE);
        TRAIL_WANDER_WEIGHT = configuration.getDouble("termites.trail.wander_weight", TRAIL_WANDER_WEIGHT);

        PHEROMONE_DIFFUSE_RATE = configuration.getDouble("pheromone.diffuse_rate", PHEROMONE_DIFFUSE_RATE);
        PHEROMONE_EVAPORATION_RATE = configuration.getDouble("pheromone.evaporation_rate", PHEROMONE_EVAPORATION_RATE);

        BOUNDARY_UP_MAX = configuration.getInt("boundary.y_up_max", BOUNDARY_UP_MAX);
        BOUNDARY_DOWN_MAX = configuration.getInt("boundary.y_down_max", BOUNDARY_DOWN_MAX);

        TIMESTEPS_BUILDER_START = configuration.getInt("timesteps.build_start", TIMESTEPS_BUILDER_START);
        TIMESTEPS_FINISH = configuration.getInt("timesteps.finish", TIMESTEPS_FINISH);
        
        System.out.print("TRAIL_WANDER_RATE: " + TRAIL_WANDER_WEIGHT + "\n");
    }
}
