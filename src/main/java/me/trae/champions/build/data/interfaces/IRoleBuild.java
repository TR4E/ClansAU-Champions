package me.trae.champions.build.data.interfaces;

import me.trae.champions.build.data.RoleSkill;
import me.trae.champions.skill.enums.SkillType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public interface IRoleBuild {

    UUID getUUID();

    String getRole();

    int getID();

    LinkedHashMap<SkillType, RoleSkill> getSkills();

    void addSkill(final RoleSkill roleSkill);

    void removeSkill(final RoleSkill roleSkill);

    RoleSkill getRoleSkillByType(final SkillType skillType);

    boolean isRoleSkillByType(final SkillType skillType);

    boolean isActive();

    void setActive(final boolean active);

    String getDisplayName();

    List<String> getEquipMessage();
}