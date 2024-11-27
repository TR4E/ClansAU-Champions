package me.trae.champions.skill.skills.global.modules.abstracts.interfaces;

import me.trae.champions.skill.types.GlobalSkill;
import org.bukkit.entity.Player;

public interface IGlobalAbility<T extends GlobalSkill<?>> {

    Class<T> getClassOfSkill();

    T getSkillByPlayer(final Player player);

    boolean isSkillByPlayer(final Player player);
}