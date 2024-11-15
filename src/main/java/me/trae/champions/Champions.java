package me.trae.champions;

import me.trae.champions.config.ConfigManager;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.weapon.WeaponManager;
import me.trae.champions.world.WorldManager;
import me.trae.core.blood.BloodManager;
import me.trae.core.energy.EnergyManager;
import me.trae.core.framework.types.plugin.MiniPlugin;

public class Champions extends MiniPlugin {

    @Override
    public void registerManagers() {
        // Core Managers
        addManager(new BloodManager<>(this));
        addManager(new EnergyManager<>(this));

        // Champions Managers
        addManager(new ConfigManager(this));
//        addManager(new GamerManager(this));
        addManager(new RoleManager(this));
        addManager(new SkillManager(this));
        addManager(new WeaponManager(this));
        addManager(new WorldManager(this));
    }
}