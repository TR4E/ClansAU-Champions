package me.trae.champions.tip;

import me.trae.champions.Champions;
import me.trae.core.tip.abstracts.AbstractTipManager;

public class TipManager extends AbstractTipManager<Champions> {

    public TipManager(final Champions instance) {
        super(instance);
    }

    @Override
    public void registerModules() {
    }
}