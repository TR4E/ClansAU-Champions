package me.trae.champions.effect;

import me.trae.champions.Champions;
import me.trae.champions.effect.types.Shock;
import me.trae.champions.effect.types.Silenced;
import me.trae.core.effect.abstracts.AbstractEffectManager;

public class EffectManager extends AbstractEffectManager<Champions> {

    public EffectManager(final Champions instance) {
        super(instance);
    }

    @Override
    public void registerModules() {
        addModule(new Shock(this));
        addModule(new Silenced(this));
    }
}