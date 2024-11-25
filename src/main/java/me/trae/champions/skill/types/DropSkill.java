package me.trae.champions.skill.types;

import me.trae.api.champions.role.Role;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.enums.PassiveSkillType;

public abstract class DropSkill<R extends Role, D extends SkillData> extends PassiveSkill<R, D> {

    public DropSkill(final R module, final PassiveSkillType passiveSkillType) {
        super(module, passiveSkillType);
    }
}