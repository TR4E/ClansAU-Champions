package me.trae.champions.build.data.interfaces;

import me.trae.champions.skill.enums.SkillType;

public interface IRoleSkill {

    SkillType getType();

    String getName();

    int getLevel();

    void setLevel(final int level);

    String getDisplayName();
}