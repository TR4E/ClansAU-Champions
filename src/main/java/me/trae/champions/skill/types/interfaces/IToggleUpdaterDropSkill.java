package me.trae.champions.skill.types.interfaces;

import me.trae.champions.skill.components.energy.EnergyNeededSkillComponent;
import me.trae.champions.skill.components.energy.EnergySkillComponent;
import me.trae.champions.skill.components.energy.EnergyUsingSkillComponent;
import me.trae.champions.skill.components.recharge.RechargeSkillComponent;
import me.trae.champions.skill.data.types.ToggleUpdaterDropSkillData;
import me.trae.champions.skill.types.models.ToggleSkill;
import org.bukkit.entity.Player;

public interface IToggleUpdaterDropSkill<D extends ToggleUpdaterDropSkillData> extends ToggleSkill<D>, EnergySkillComponent, EnergyNeededSkillComponent, EnergyUsingSkillComponent, RechargeSkillComponent {

    void onUsing(final Player player, final D data);

    boolean isUsingByPlayer(final Player player);
}