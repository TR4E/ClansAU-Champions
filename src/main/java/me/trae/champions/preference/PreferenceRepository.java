package me.trae.champions.preference;

import me.trae.champions.Champions;
import me.trae.champions.config.Config;
import me.trae.core.preference.abstracts.AbstractPreferenceRepository;

public class PreferenceRepository extends AbstractPreferenceRepository<Champions, PreferenceManager, Config> {

    public PreferenceRepository(final PreferenceManager manager) {
        super(manager);
    }

    @Override
    public Class<Config> getClassOfConfiguration() {
        return Config.class;
    }
}