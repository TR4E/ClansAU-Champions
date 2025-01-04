package me.trae.champions.build.data;

import me.trae.api.champions.skill.Skill;
import me.trae.champions.build.data.interfaces.IRoleSkill;
import me.trae.champions.skill.enums.SkillType;
import me.trae.core.utility.UtilString;

import java.util.Arrays;

public class RoleSkill implements IRoleSkill {

    private final SkillType skillType;
    private final String name;

    private int level;

    public RoleSkill(final SkillType skillType, final String name, final int level) {
        this.skillType = skillType;
        this.name = name;
        this.level = level;
    }

    public RoleSkill(final Skill<?, ?> skill, final int level) {
        this(skill.getType(), skill.getName(), level);
    }

    public RoleSkill(final String[] tokens) {
        this(SkillType.valueOf(tokens[0]), UtilString.unSlice(tokens[1]), Integer.parseInt(tokens[2]));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public SkillType getType() {
        return this.skillType;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public void setLevel(final int level) {
        this.level = level;
    }

    @Override
    public String getDisplayName() {
        return UtilString.format("%s %s", this.getName(), this.getLevel());
    }

    @Override
    public String toString() {
        return String.join(":", Arrays.asList(this.getType().name(), UtilString.slice(this.getName()), String.valueOf(this.getLevel())));
    }
}