package me.trae.champions.skill.types.interfaces;

import me.trae.champions.skill.components.energy.EnergyNeededSkillComponent;
import me.trae.champions.skill.components.energy.EnergyUsingSkillComponent;
import me.trae.champions.skill.data.SkillData;
import org.bukkit.entity.Player;

public interface IChannelSkill<D extends SkillData> extends EnergyNeededSkillComponent, EnergyUsingSkillComponent {

    void onUsing(final Player player, final D data);

    boolean isUsingByPlayer(final Player player);
}