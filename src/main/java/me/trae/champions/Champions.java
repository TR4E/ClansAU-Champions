package me.trae.champions;

import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.weapon.WeaponManager;
import me.trae.core.framework.types.plugin.MiniPlugin;

public class Champions extends MiniPlugin {

    @Override
    public void registerManagers() {
        addManager(new RoleManager(this));
        addManager(new SkillManager(this));
        addManager(new WeaponManager(this));
    }
}