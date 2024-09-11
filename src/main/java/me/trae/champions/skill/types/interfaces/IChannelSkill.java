package me.trae.champions.skill.types.interfaces;

import me.trae.champions.skill.data.SkillData;
import org.bukkit.entity.Player;

public interface IChannelSkill<D extends SkillData> {

    void onUsing(final Player player, final D data);
}