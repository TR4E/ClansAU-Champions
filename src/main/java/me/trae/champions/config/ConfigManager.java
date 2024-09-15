package me.trae.champions.config;

import me.trae.champions.Champions;
import me.trae.core.config.abstracts.AbstractConfig;
import me.trae.core.config.abstracts.AbstractConfigManager;

public class ConfigManager extends AbstractConfigManager<Champions> {

    public ConfigManager(final Champions instance) {
        super(instance);
    }

    @Override
    public AbstractConfig createConfig(final String name) {
        return new Config(name);
    }
}