package me.trae.champions.skill.types;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.skill.Skill;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;

public abstract class GlobalSkill<R extends Role, D extends SkillData> extends Skill<R, D> {

    public GlobalSkill(final R module) {
        super(module, SkillType.GLOBAL);
    }
}