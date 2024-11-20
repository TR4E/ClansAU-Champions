package me.trae.champions.skill;

import me.trae.champions.Champions;
import me.trae.champions.skill.interfaces.ISkillManager;
import me.trae.champions.skill.modules.HandleBoosterWeaponOnSkillLevel;
import me.trae.champions.skill.modules.HandleSkillDataExpirations;
import me.trae.champions.skill.modules.HandleSkillRechargeProgressBar;
import me.trae.champions.skill.modules.activation.HandleActiveSkillActivation;
import me.trae.champions.skill.modules.activation.HandleChannelSkillUsing;
import me.trae.core.framework.SpigotManager;

public class SkillManager extends SpigotManager<Champions> implements ISkillManager {

    public SkillManager(final Champions instance) {
        super(instance);
    }

    @Override
    public void registerModules() {
        // Activation Modules
        addModule(new HandleActiveSkillActivation(this));
        addModule(new HandleChannelSkillUsing(this));

        // Modules
        addModule(new HandleBoosterWeaponOnSkillLevel(this));
        addModule(new HandleSkillDataExpirations(this));
        addModule(new HandleSkillRechargeProgressBar(this));
    }
}