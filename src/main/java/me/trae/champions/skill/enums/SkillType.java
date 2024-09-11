package me.trae.champions.skill.enums;

import me.trae.champions.skill.enums.interfaces.ISkillType;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.enums.ActionType;

public enum SkillType implements ISkillType {

    SWORD, AXE, BOW, PASSIVE_A, PASSIVE_B, GLOBAL;

    private final String name;

    SkillType() {
        this.name = UtilString.clean(this.name());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ActionType getActionType() {
        switch (this) {
            case SWORD:
            case AXE:
                return ActionType.RIGHT_CLICK;
            case BOW:
                return ActionType.LEFT_CLICK;
        }

        return null;
    }
}