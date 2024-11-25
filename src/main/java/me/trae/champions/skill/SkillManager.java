package me.trae.champions.skill;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.skill.Skill;
import me.trae.champions.Champions;
import me.trae.champions.build.data.RoleBuild;
import me.trae.champions.build.data.RoleSkill;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.components.energy.EnergySkillComponent;
import me.trae.champions.skill.components.recharge.RechargeSkillComponent;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.interfaces.ISkillManager;
import me.trae.champions.skill.modules.HandleBoosterWeaponOnSkillLevel;
import me.trae.champions.skill.modules.HandleSkillDataExpirations;
import me.trae.champions.skill.modules.HandleSkillRechargeProgressBar;
import me.trae.champions.skill.modules.activation.*;
import me.trae.core.Core;
import me.trae.core.energy.EnergyManager;
import me.trae.core.framework.SpigotManager;
import me.trae.core.recharge.RechargeManager;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.components.SelfManagedAbilityComponent;
import org.bukkit.entity.Player;

public class SkillManager extends SpigotManager<Champions> implements ISkillManager {

    public SkillManager(final Champions instance) {
        super(instance);
    }

    @Override
    public void registerModules() {
        // Activation Modules
        addModule(new HandleActiveBowSkillActivation(this));
        addModule(new HandleActiveSkillActivation(this));
        addModule(new HandleBowSkillUpdater(this));
        addModule(new HandleChannelSkillUsing(this));
        addModule(new HandleDropSkillActivation(this));
        addModule(new HandlePassiveBowSkillActivation(this));

        // Modules
        addModule(new HandleBoosterWeaponOnSkillLevel(this));
        addModule(new HandleSkillDataExpirations(this));
        addModule(new HandleSkillRechargeProgressBar(this));
    }

    @Override
    public <T extends Skill<?, ?>> T getSkillByType(final Class<T> clazz, final Player player, final SkillType skillType) {
        if (skillType == null) {
            return null;
        }

        final Role role = this.getInstance().getManagerByClass(RoleManager.class).getPlayerRole(player);
        if (role == null) {
            return null;
        }

        final RoleBuild roleBuild = role.getRoleBuildByPlayer(player);
        if (roleBuild == null) {
            return null;
        }

        final RoleSkill roleSkill = roleBuild.getRoleSkillByType(skillType);
        if (roleSkill == null) {
            return null;
        }

        final T skill = UtilJava.cast(clazz, role.getSubModuleByName(roleSkill.getName()));
        if (skill == null) {
            return null;
        }

        if (!(clazz.isInstance(skill))) {
            return null;
        }

        return UtilJava.cast(clazz, skill);
    }

    @Override
    public boolean canActivateActiveSkill(final Player player, final Skill<?, ?> skill) {
        if (!(skill.canActivate(player))) {
            return false;
        }

        final int level = skill.getLevel(player);
        if (level == 0) {
            return false;
        }

        if (!(skill instanceof SelfManagedAbilityComponent)) {

            final RechargeManager rechargeManager = this.getInstance(Core.class).getManagerByClass(RechargeManager.class);

            if (skill instanceof RechargeSkillComponent) {
                final RechargeSkillComponent rechargeSkillComponent = UtilJava.cast(RechargeSkillComponent.class, skill);

                if (rechargeSkillComponent.hasRecharge(level)) {
                    if (rechargeManager.isCooling(player, skill.getName(), true)) {
                        return false;
                    }
                }
            }

            if (skill instanceof EnergySkillComponent) {
                final EnergySkillComponent energySkillComponent = UtilJava.cast(EnergySkillComponent.class, skill);

                if (energySkillComponent.hasEnergy(level)) {
                    final EnergyManager energyManager = this.getInstance(Core.class).getManagerByClass(EnergyManager.class);

                    if (!(energyManager.use(player, skill.getName(), energySkillComponent.getEnergy(level), true))) {
                        return false;
                    }
                }
            }

            if (skill instanceof RechargeSkillComponent) {
                final RechargeSkillComponent rechargeSkillComponent = UtilJava.cast(RechargeSkillComponent.class, skill);

                if (rechargeSkillComponent.hasRecharge(level)) {
                    if (!(rechargeManager.add(player, skill.getName(), rechargeSkillComponent.getRecharge(level), true))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}