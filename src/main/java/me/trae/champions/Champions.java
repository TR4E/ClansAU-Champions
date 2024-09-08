package me.trae.champions;

import me.trae.champions.weapon.WeaponManager;
import me.trae.core.framework.types.plugin.MiniPlugin;

public class Champions extends MiniPlugin {

    @Override
    public void registerManagers() {
        addManager(new WeaponManager(this));
    }
}