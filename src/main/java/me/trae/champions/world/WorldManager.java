package me.trae.champions.world;

import me.trae.champions.Champions;
import me.trae.champions.world.modules.DisableShootingArrowsWhileInLiquid;
import me.trae.core.world.abstracts.AbstractWorldManager;

public class WorldManager extends AbstractWorldManager<Champions> {

    public WorldManager(final Champions instance) {
        super(instance);
    }

    @Override
    public void registerModules() {
        addModule(new DisableShootingArrowsWhileInLiquid(this));
    }
}