package me.trae.champions.skill.types.interfaces;

import me.trae.champions.skill.components.energy.EnergySkillComponent;
import me.trae.champions.skill.components.recharge.RechargeSkillComponent;
import org.bukkit.entity.Player;

public interface IActiveSkill extends EnergySkillComponent, RechargeSkillComponent {

    void onActivate(final Player player, final int level);

    boolean isActive(final Player player);
}