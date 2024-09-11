package me.trae.champions.skill.types;

import me.trae.champions.role.Role;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.interfaces.IChannelSkill;

public abstract class ChannelSkill<R extends Role, D extends SkillData> extends ActiveSkill<R, D> implements IChannelSkill<D> {

    public ChannelSkill(final R module) {
        super(module, SkillType.SWORD);
    }
}