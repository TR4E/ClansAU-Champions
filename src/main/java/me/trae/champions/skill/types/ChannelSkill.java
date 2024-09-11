package me.trae.champions.skill.types;

import me.trae.champions.role.Role;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.interfaces.IChannelSkill;
import me.trae.core.utility.enums.ActionType;

public abstract class ChannelSkill<R extends Role, D extends SkillData> extends ActiveSkill<R, D> implements IChannelSkill<D> {

    public ChannelSkill(final R module) {
        super(module, ActionType.RIGHT_CLICK);
    }
}