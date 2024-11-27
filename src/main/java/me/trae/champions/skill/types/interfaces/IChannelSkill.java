package me.trae.champions.skill.types.interfaces;

import me.trae.champions.skill.components.energy.EnergyNeededChannelSkillComponent;
import me.trae.champions.skill.components.energy.EnergyUsingChannelSkillComponent;
import me.trae.champions.skill.data.SkillData;
import org.bukkit.entity.Player;

public interface IChannelSkill<D extends SkillData> extends EnergyNeededChannelSkillComponent, EnergyUsingChannelSkillComponent {

    void onUsing(final Player player, final D data);

    boolean isUsingByPlayer(final Player player);
}