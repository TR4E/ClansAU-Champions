package me.trae.champions.skill.skills.global.modules.abstracts;

import me.trae.api.champions.skill.events.SkillPreActivateEvent;
import me.trae.champions.Champions;
import me.trae.champions.effect.EffectManager;
import me.trae.champions.effect.types.Silenced;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.skills.global.modules.abstracts.interfaces.IGlobalAbility;
import me.trae.champions.skill.types.GlobalSkill;
import me.trae.core.Core;
import me.trae.core.client.ClientManager;
import me.trae.core.energy.EnergyManager;
import me.trae.core.framework.SpigotModule;
import me.trae.core.recharge.RechargeManager;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilServer;
import org.bukkit.entity.Player;

import java.util.Collections;

public abstract class GlobalAbility<T extends GlobalSkill<?>> extends SpigotModule<Champions, SkillManager> implements IGlobalAbility<T> {

    public GlobalAbility(final SkillManager manager) {
        super(manager);
    }

    @Override
    public T getSkillByPlayer(final Player player) {
        return this.getManager().getSkillByType(this.getClassOfSkill(), player, SkillType.GLOBAL);
    }

    @Override
    public boolean isSkillByPlayer(final Player player) {
        return this.getSkillByPlayer(player) != null;
    }

    @Override
    public boolean canActivate(final Player player, final T skill) {
        final int level = skill.getLevel(player);
        if (level == 0) {
            return false;
        }

        if (!(this.getInstanceByClass(Core.class).getManagerByClass(ClientManager.class).getClientByPlayer(player).isAdministrating())) {
            if (UtilServer.getEvent(new SkillPreActivateEvent(skill, player)).isCancelled()) {
                return false;
            }

            if (this.getInstanceByClass().getManagerByClass(EffectManager.class).getModuleByClass(Silenced.class).isUserByEntity(player)) {
                UtilMessage.simpleMessage(player, "Skill", "You cannot use <green><var></green> while silenced.", Collections.singletonList(this.getName()));
                return false;
            }

            final RechargeManager rechargeManager = this.getInstanceByClass(Core.class).getManagerByClass(RechargeManager.class);

            if (this.hasRecharge(level)) {
                if (rechargeManager.isCooling(player, skill.getName(), false)) {
                    return false;
                }
            }

            if (this.hasEnergy(level)) {
                if (!(this.getInstanceByClass(Core.class).getManagerByClass(EnergyManager.class).use(player, skill.getName(), this.getEnergy(level), true))) {
                    return false;
                }
            }

            if (this.hasRecharge(level) && !(rechargeManager.add(player, skill.getName(), this.getRecharge(level), false))) {
                return false;
            }
        }

        return true;
    }
}