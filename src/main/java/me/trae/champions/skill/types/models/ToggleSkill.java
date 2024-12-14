package me.trae.champions.skill.types.models;

import me.trae.champions.skill.data.SkillData;
import org.bukkit.entity.Player;

public interface ToggleSkill<D extends SkillData> {

    void onDeActivate(final Player player, final D data);
}