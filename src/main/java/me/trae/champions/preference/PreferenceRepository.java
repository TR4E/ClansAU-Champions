package me.trae.champions.preference;

import me.trae.champions.Champions;
import me.trae.core.preference.abstracts.AbstractPreferenceRepository;

public class PreferenceRepository extends AbstractPreferenceRepository<Champions, PreferenceManager> {

    public PreferenceRepository(final PreferenceManager manager) {
        super(manager);
    }
}