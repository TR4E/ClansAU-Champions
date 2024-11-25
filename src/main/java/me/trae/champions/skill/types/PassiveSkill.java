package me.trae.champions.skill.types;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.skill.Skill;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.enums.PassiveSkillType;

public abstract class PassiveSkill<R extends Role, D extends SkillData> extends Skill<R, D> {

    public PassiveSkill(final R module, final PassiveSkillType passiveSkillType) {
        super(module, SkillType.valueOf(passiveSkillType.name()));
    }
}