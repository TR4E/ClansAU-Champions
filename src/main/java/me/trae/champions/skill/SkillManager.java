package me.trae.champions.skill;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.skill.Skill;
import me.trae.champions.Champions;
import me.trae.champions.build.data.RoleBuild;
import me.trae.champions.build.data.RoleSkill;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.interfaces.ISkillManager;
import me.trae.champions.skill.modules.HandleBoosterWeaponOnSkillLevel;
import me.trae.champions.skill.modules.HandleSkillDataExpirations;
import me.trae.champions.skill.modules.HandleSkillRechargeProgressBar;
import me.trae.champions.skill.modules.activation.*;
import me.trae.champions.skill.modules.friendlyfire.DisableSkillFriendlyFireWhileAdministrating;
import me.trae.champions.skill.modules.friendlyfire.DisableSkillFriendlyFireWhileVanished;
import me.trae.champions.skill.skills.global.modules.SwimAbility;
import me.trae.core.framework.SpigotManager;
import me.trae.core.utility.UtilJava;
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
        addModule(new HandleToggleUpdaterDropSkillUsing(this));

        // Friendly Fire Modules
        addModule(new DisableSkillFriendlyFireWhileAdministrating(this));
        addModule(new DisableSkillFriendlyFireWhileVanished(this));

        // Modules
        addModule(new HandleBoosterWeaponOnSkillLevel(this));
        addModule(new HandleSkillDataExpirations(this));
        addModule(new HandleSkillRechargeProgressBar(this));

        // Global Skill Abilities
        addModule(new SwimAbility(this));
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

        if (!(role.isEnabled())) {
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

        if (!(skill.isEnabled())) {
            return null;
        }

        if (!(clazz.isInstance(skill))) {
            return null;
        }

        return UtilJava.cast(clazz, skill);
    }
}