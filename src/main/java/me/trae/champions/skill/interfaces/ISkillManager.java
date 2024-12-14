package me.trae.champions.skill.interfaces;

import me.trae.api.champions.skill.Skill;
import me.trae.champions.skill.enums.SkillType;
import org.bukkit.entity.Player;

public interface ISkillManager {

    <T extends Skill<?, ?>> T getSkillByType(final Class<T> clazz, final Player player, final SkillType skillType);
}