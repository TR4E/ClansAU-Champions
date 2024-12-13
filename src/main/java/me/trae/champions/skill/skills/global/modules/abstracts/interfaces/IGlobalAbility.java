package me.trae.champions.skill.skills.global.modules.abstracts.interfaces;

import me.trae.champions.skill.components.energy.EnergySkillComponent;
import me.trae.champions.skill.components.recharge.RechargeSkillComponent;
import me.trae.champions.skill.types.GlobalSkill;
import org.bukkit.entity.Player;

public interface IGlobalAbility<T extends GlobalSkill<?>> extends EnergySkillComponent, RechargeSkillComponent {

    Class<T> getClassOfSkill();

    T getSkillByPlayer(final Player player);

    boolean isSkillByPlayer(final Player player);

    boolean canActivate(final Player player, final T skill);
}