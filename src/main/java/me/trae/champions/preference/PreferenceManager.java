package me.trae.champions.preference;

import me.trae.champions.Champions;
import me.trae.champions.preference.types.DisplayRaveArmour;
import me.trae.core.preference.abstracts.AbstractPreferenceManager;

public class PreferenceManager extends AbstractPreferenceManager<Champions, PreferenceRepository> {

    public PreferenceManager(final Champions instance) {
        super(instance);
    }

    @Override
    public void registerModules() {
        super.registerModules();

        addModule(new DisplayRaveArmour(this));
    }

    @Override
    public Class<PreferenceRepository> getClassOfRepository() {
        return PreferenceRepository.class;
    }
}