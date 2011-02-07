package com.bukkit.mcnestbuilder.plugin;

import org.bukkit.event.block.BlockListener;

/**
 * <pluginname> block listener
 * @author <yourname>
 */
public class MCNestBuilderBlockListener extends BlockListener {
    private final MCNestBuilder plugin;

    public MCNestBuilderBlockListener(final MCNestBuilder plugin) {
        this.plugin = plugin;
    }

    //put all Block related code here
}