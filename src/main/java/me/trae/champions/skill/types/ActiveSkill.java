package me.trae.champions.skill.types;

import me.trae.champions.role.Role;
import me.trae.champions.skill.Skill;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.interfaces.IActiveSkill;
import me.trae.core.utility.enums.ActionType;

public abstract class ActiveSkill<R extends Role, D extends SkillData> extends Skill<R, D> implements IActiveSkill {

    private final ActionType actionType;

    public ActiveSkill(final R module, final ActionType actionType) {
        super(module);

        this.actionType = actionType;
    }

    @Override
    public ActionType getActionType() {
        return this.actionType;
    }
}