package me.trae.champions.skill.modules.activation;

import me.trae.api.champions.role.Role;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.types.ToggleUpdaterDropSkill;
import me.trae.champions.skill.types.models.ToggleSkill;
import me.trae.champions.weapon.models.PassiveActivatorWeapon;
import me.trae.core.Core;
import me.trae.core.energy.EnergyManager;
import me.trae.core.framework.types.frame.SpigotUpdater;
import me.trae.core.recharge.RechargeManager;
import me.trae.core.updater.annotations.Update;
import me.trae.core.utility.UtilJava;
import me.trae.core.weapon.WeaponManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HandleToggleUpdaterDropSkillUsing extends SpigotUpdater<Champions, SkillManager> {

    public HandleToggleUpdaterDropSkillUsing(final SkillManager manager) {
        super(manager);
    }

    @Update(delay = 50L)
    public void onUpdater() {
        for (final Role role : this.getInstanceByClass().getManagerByClass(RoleManager.class).getModulesByClass(Role.class)) {
            for (final ToggleUpdaterDropSkill<?, ?> skill : role.getSkillsByClass(ToggleUpdaterDropSkill.class)) {
                skill.getUsers().values().removeIf(data -> {
                    final Player player = Bukkit.getPlayer(data.getUUID());
                    if (player == null) {
                        return true;
                    }

                    if (!(this.canActivateSkill(player, skill))) {
                        skill.reset(player);
                        UtilJava.cast(ToggleSkill.class, skill).onDeActivate(player, UtilJava.matchlessObjectCast(skill.getClassOfData(), data));
                        return true;
                    }

                    data.setUsing(true);

                    skill.onUsing(player, UtilJava.matchlessObjectCast(skill.getClassOfData(), data));
                    return false;
                });
            }
        }
    }

    private boolean canActivateSkill(final Player player, final ToggleUpdaterDropSkill<?, ?> skill) {
        if (!(this.getInstanceByClass(Core.class).getManagerByClass(WeaponManager.class).getWeaponByItemStack(player.getEquipment().getItemInHand()) instanceof PassiveActivatorWeapon)) {
            return false;
        }

        final int level = skill.getLevel(player);
        if (level == 0) {
            return false;
        }

        if (!(skill.canActivate(player))) {
            return false;
        }

        final EnergyManager energyManager = this.getInstanceByClass(Core.class).getManagerByClass(EnergyManager.class);

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
                    final RechargeManager rechargeManager = this.getInstanceByClass(Core.class).getManagerByClass(RechargeManager.class);

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