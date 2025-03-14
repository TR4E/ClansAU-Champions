package me.trae.champions.skill.modules.activation;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.skill.events.SkillLocationEvent;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.types.ToggleUpdaterDropSkill;
import me.trae.champions.skill.types.models.ToggleSkill;
import me.trae.champions.weapon.models.PassiveActivatorWeapon;
import me.trae.core.energy.EnergyManager;
import me.trae.core.framework.types.frame.SpigotUpdater;
import me.trae.core.recharge.RechargeManager;
import me.trae.core.updater.annotations.Update;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.UtilServer;
import me.trae.core.utility.injectors.annotations.Inject;
import me.trae.core.weapon.WeaponManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HandleToggleUpdaterDropSkillUsing extends SpigotUpdater<Champions, SkillManager> {

    @Inject
    private RoleManager roleManager;

    @Inject
    private WeaponManager weaponManager;

    @Inject
    private EnergyManager energyManager;

    @Inject
    private RechargeManager rechargeManager;

    public HandleToggleUpdaterDropSkillUsing(final SkillManager manager) {
        super(manager);
    }

    @Update(delay = 50L)
    public void onUpdater() {
        for (final Role role : this.roleManager.getModulesByClass(Role.class)) {
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
        if (!(this.weaponManager.getWeaponByItemStack(player.getEquipment().getItemInHand()) instanceof PassiveActivatorWeapon)) {
            return false;
        }

        final int level = skill.getLevel(player);
        if (level == 0) {
            return false;
        }

        if (UtilServer.getEvent(new SkillLocationEvent(skill, player, player.getLocation())).isCancelled()) {
            return false;
        }

        if (!(skill.canActivate(player))) {
            return false;
        }

        if (!(skill.isUsingByPlayer(player))) {
            if (skill.hasEnergyNeeded(level)) {
                if (this.energyManager.isExhausted(player, skill.getName(), skill.getEnergyNeeded(level), true)) {
                    return false;
                }
            }
        }

        if (skill.hasEnergyUsing(level)) {
            if (!(this.energyManager.use(player, skill.getName(), skill.getEnergyUsing(level), true))) {
                if (skill.hasRecharge(level)) {
                    if (!(this.rechargeManager.add(player, skill.getName(), skill.getRecharge(level), true))) {
                        return false;
                    }
                }

                return false;
            }
        }

        return true;
    }
}