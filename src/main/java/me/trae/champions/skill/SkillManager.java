package me.trae.champions.skill;

import me.trae.champions.Champions;
import me.trae.champions.skill.modules.HandleActiveSkillActivation;
import me.trae.champions.skill.modules.HandleChannelSkillUsing;
import me.trae.champions.skill.modules.HandleSkillDataExpirations;
import me.trae.core.framework.SpigotManager;

public class SkillManager extends SpigotManager<Champions> {

    public SkillManager(final Champions instance) {
        super(instance);
    }

    @Override
    public void registerModules() {
        addModule(new HandleActiveSkillActivation(this));
        addModule(new HandleChannelSkillUsing(this));
        addModule(new HandleSkillDataExpirations(this));
    }
}