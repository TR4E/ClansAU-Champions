package me.trae.champions.weapon;

import me.trae.champions.Champions;
import me.trae.champions.weapon.weapons.items.ExtinguishingPotion;
import me.trae.champions.weapon.weapons.items.GravityGrenade;
import me.trae.champions.weapon.weapons.items.IncendiaryGrenade;
import me.trae.champions.weapon.weapons.items.ThrowingWeb;
import me.trae.champions.weapon.weapons.pvp.*;
import me.trae.core.weapon.abstracts.AbstractWeaponManager;

public class WeaponManager extends AbstractWeaponManager<Champions> {

    public WeaponManager(final Champions instance) {
        super(instance);
    }

    @Override
    public void registerModules() {
        // Custom Items
        addModule(new ExtinguishingPotion(this));
        addModule(new GravityGrenade(this));
        addModule(new IncendiaryGrenade(this));
        addModule(new ThrowingWeb(this));

        // Champions PvP Weapons
        addModule(new BoosterAxe(this));
        addModule(new BoosterBow(this));
        addModule(new BoosterSword(this));

        addModule(new PowerAxe(this));
        addModule(new PowerSword(this));

        addModule(new StandardAxe(this));
        addModule(new StandardBow(this));
        addModule(new StandardSword(this));
    }
}