package me.trae.champions.skill.types;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.skill.Skill;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.skills.global.modules.abstracts.GlobalAbility;
import me.trae.champions.skill.types.interfaces.IGlobalSkill;

public abstract class GlobalSkill<T extends GlobalAbility<?>> extends Skill<Role, SkillData> implements IGlobalSkill<T> {

    public GlobalSkill(final Role module) {
        super(module, SkillType.GLOBAL);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    @Override
    public T getAbility() {
        return this.getInstanceByClass().getManagerByClass(SkillManager.class).getModuleByClass(this.getClassOfAbility());
    }
}