package me.trae.champions.skill.skills.global.modules.abstracts;

import me.trae.api.champions.skill.events.SkillPreActivateEvent;
import me.trae.champions.Champions;
import me.trae.champions.effect.types.Silenced;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.skills.global.modules.abstracts.interfaces.IGlobalAbility;
import me.trae.champions.skill.types.GlobalSkill;
import me.trae.core.client.ClientManager;
import me.trae.core.energy.EnergyManager;
import me.trae.core.framework.SpigotModule;
import me.trae.core.recharge.RechargeManager;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilServer;
import me.trae.core.utility.injectors.annotations.Inject;
import org.bukkit.entity.Player;

import java.util.Collections;

public abstract class GlobalAbility<T extends GlobalSkill<?>> extends SpigotModule<Champions, SkillManager> implements IGlobalAbility<T> {

    @Inject
    private ClientManager clientManager;

    @Inject
    private Silenced silencedEffect;

    @Inject
    private RechargeManager rechargeManager;

    @Inject
    private EnergyManager energyManager;

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

        if (!(this.clientManager.getClientByPlayer(player).isAdministrating())) {
            if (UtilServer.getEvent(new SkillPreActivateEvent(skill, player)).isCancelled()) {
                return false;
            }

            if (this.silencedEffect.isUserByEntity(player)) {
                UtilMessage.simpleMessage(player, "Skill", "You cannot use <green><var></green> while silenced.", Collections.singletonList(this.getName()));
                return false;
            }

            if (this.hasRecharge(level)) {
                if (this.rechargeManager.isCooling(player, skill.getName(), false)) {
                    return false;
                }
            }

            if (this.hasEnergy(level)) {
                if (!(this.energyManager.use(player, skill.getName(), this.getEnergy(level), true))) {
                    return false;
                }
            }

            if (this.hasRecharge(level) && !(this.rechargeManager.add(player, skill.getName(), this.getRecharge(level), false))) {
                return false;
            }
        }

        return true;
    }
}