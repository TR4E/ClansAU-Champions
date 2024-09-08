package me.trae.champions.weapon;

import me.trae.champions.Champions;
import me.trae.champions.weapon.weapons.*;
import me.trae.core.weapon.abstracts.AbstractWeaponManager;

public class WeaponManager extends AbstractWeaponManager<Champions> {

    public WeaponManager(final Champions instance) {
        super(instance);
    }

    @Override
    public void registerModules() {
        addModule(new BoosterAxe(this));
        addModule(new BoosterSword(this));

        addModule(new PowerAxe(this));
        addModule(new PowerSword(this));

        addModule(new StandardAxe(this));
        addModule(new StandardSword(this));
    }
}