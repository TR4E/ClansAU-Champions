package me.trae.champions.config;

import me.trae.champions.Champions;
import me.trae.core.config.abstracts.AbstractConfig;
import me.trae.core.utility.UtilPlugin;

public class Config extends AbstractConfig {

    public Config(final String folderName, final String name) {
        super(UtilPlugin.getInstance(Champions.class), folderName, name);
    }
}