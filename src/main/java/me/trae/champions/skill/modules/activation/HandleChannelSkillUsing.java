package me.trae.champions.skill.modules.activation;

import me.trae.api.champions.role.Role;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.types.ChannelSkill;
import me.trae.core.Core;
import me.trae.core.energy.EnergyManager;
import me.trae.core.framework.types.frame.SpigotUpdater;
import me.trae.core.recharge.RechargeManager;
import me.trae.core.updater.annotations.Update;
import me.trae.core.utility.UtilJava;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HandleChannelSkillUsing extends SpigotUpdater<Champions, SkillManager> {

    public HandleChannelSkillUsing(final SkillManager manager) {
        super(manager);
    }

    @Update(delay = 50L)
    public void onUpdater() {
        for (final Role role : this.getInstance().getManagerByClass(RoleManager.class).getModulesByClass(Role.class)) {
            for (final ChannelSkill<?, ?> skill : role.getSkillsByClass(ChannelSkill.class)) {
                skill.getUsers().values().removeIf(data -> {
                    final Player player = Bukkit.getPlayer(data.getUUID());
                    if (player == null) {
                        return true;
                    }

                    if (!(this.canActivate(player, skill))) {
                        skill.reset(player);
                        return true;
                    }

                    data.setUsing(true);

                    skill.onUsing(player, UtilJava.matchlessObjectCast(skill.getClassOfData(), data));
                    return false;
                });
            }
        }
    }

    private boolean canActivate(final Player player, final ChannelSkill<?, ?> skill) {
        if (!(player.isBlocking())) {
            return false;
        }

        if (!(skill.canActivate(player))) {
            return false;
        }

        final int level = skill.getLevel(player);
        if (level == 0) {
            return false;
        }

        final EnergyManager energyManager = this.getInstance(Core.class).getManagerByClass(EnergyManager.class);

        if (!(skill.isUsingByPlayer(player))) {
            if (skill.hasEnergyNeeded(level)) {
                if (energyManager.isExhausted(player, skill.getName(), skill.getEnergyNeeded(level), true)) {
                    return false;
                }
            }
        }

        if (skill.hasEnergyUsing(level)) {
            if (!(energyManager.use(player, skill.getName(), skill.getEnergyUsing(level), true))) {
                if (skill.hasRecharge(level)) {
                    final RechargeManager rechargeManager = this.getInstance(Core.class).getManagerByClass(RechargeManager.class);

                    if (!(rechargeManager.add(player, skill.getName(), skill.getRecharge(level), true))) {
                        return false;
                    }
                }

                return false;
            }
        }

        return true;
    }
}