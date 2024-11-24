package me.trae.champions.build.menus.skill.buttons.interfaces;

import me.trae.api.champions.skill.Skill;
import me.trae.champions.build.data.RoleSkill;

public interface ISkillSelectButton {

    Skill<?, ?> getSkill();

    RoleSkill getRoleSkill();
}