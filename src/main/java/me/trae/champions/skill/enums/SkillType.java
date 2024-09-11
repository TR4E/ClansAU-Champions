package me.trae.champions.skill.enums;

import me.trae.core.utility.UtilString;

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
}