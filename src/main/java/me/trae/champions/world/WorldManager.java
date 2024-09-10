package me.trae.champions.world;

import me.trae.champions.Champions;
import me.trae.champions.world.modules.DisableShootingArrowsWhileInLiquid;
import me.trae.core.framework.SpigotManager;

public class WorldManager extends SpigotManager<Champions> {

    public WorldManager(final Champions instance) {
        super(instance);
    }

    @Override
    public void registerModules() {
        addModule(new DisableShootingArrowsWhileInLiquid(this));
    }
}