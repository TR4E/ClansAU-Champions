package me.trae.champions.skill.types.interfaces;

import me.trae.champions.skill.skills.global.modules.abstracts.GlobalAbility;

public interface IGlobalSkill<T extends GlobalAbility<?>> {

    Class<T> getClassOfAbility();

    T getAbility();
}