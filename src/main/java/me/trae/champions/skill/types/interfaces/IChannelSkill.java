package me.trae.champions.skill.types.interfaces;

import me.trae.champions.skill.components.EnergyUsingSkillComponent;
import me.trae.champions.skill.data.SkillData;
import org.bukkit.entity.Player;

public interface IChannelSkill<D extends SkillData> extends EnergyUsingSkillComponent {

    void onUsing(final Player player, final D data);
}