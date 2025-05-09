package me.trae.champions.skill.modules;

import me.trae.api.champions.role.Role;
import me.trae.champions.Champions;
import me.trae.champions.build.data.RoleBuild;
import me.trae.champions.build.data.RoleSkill;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.core.framework.types.frame.SpigotUpdater;
import me.trae.core.recharge.Recharge;
import me.trae.core.recharge.RechargeManager;
import me.trae.core.recharge.events.RechargeProgressBarCheckEvent;
import me.trae.core.updater.annotations.Update;
import me.trae.core.utility.UtilActionBar;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.UtilServer;
import me.trae.core.utility.injectors.annotations.Inject;
import me.trae.core.weapon.WeaponManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HandleSkillRechargeProgressBar extends SpigotUpdater<Champions, SkillManager> {

    private final int PRIORITY = 5;

    @Inject
    private RoleManager roleManager;

    @Inject
    private RechargeManager rechargeManager;

    @Inject
    private WeaponManager weaponManager;

    public HandleSkillRechargeProgressBar(final SkillManager manager) {
        super(manager);
    }

    @Update(delay = 100L)
    public void onUpdater() {
        for (final Player player : UtilServer.getOnlinePlayers()) {
            if (UtilServer.getEvent(new RechargeProgressBarCheckEvent(player)).isCancelled()) {
                UtilActionBar.removeActionBar(player, this.PRIORITY);
                continue;
            }

            final ItemStack itemStack = player.getEquipment().getItemInHand();
            if (itemStack == null) {
                UtilActionBar.removeActionBar(player, this.PRIORITY);
                continue;
            }

            final Role role = this.roleManager.getPlayerRole(player);
            if (role == null) {
                UtilActionBar.removeActionBar(player, this.PRIORITY);
                continue;
            }

            final RoleBuild roleBuild = role.getRoleBuildByPlayer(player);
            if (roleBuild == null) {
                UtilActionBar.removeActionBar(player, this.PRIORITY);
                continue;
            }

            final SkillType skillType = SkillType.getByMaterial(itemStack.getType());
            if (skillType == null) {
                UtilActionBar.removeActionBar(player, this.PRIORITY);
                continue;
            }

            final RoleSkill roleSkill = roleBuild.getRoleSkillByType(skillType);
            if (roleSkill == null) {
                UtilActionBar.removeActionBar(player, this.PRIORITY);
                continue;
            }

            final ActiveSkill<?, ?> skill = UtilJava.cast(ActiveSkill.class, role.getSubModuleByName(roleSkill.getName()));
            if (skill == null) {
                UtilActionBar.removeActionBar(player, this.PRIORITY);
                continue;
            }

            final int level = skill.getLevel(player);

            if (level == 0) {
                UtilActionBar.removeActionBar(player, this.PRIORITY);
                continue;
            }

            if (!(skill.hasRecharge(level))) {
                UtilActionBar.removeActionBar(player, this.PRIORITY);
                continue;
            }

            final Recharge recharge = this.rechargeManager.getRechargeByName(player, skill.getName());
            if (recharge == null) {
                if (!(UtilActionBar.TEXT_MAP.getOrDefault(player, "").contains("Recharged"))) {
                    UtilActionBar.removeActionBar(player, this.PRIORITY);
                }
                continue;
            }

            UtilActionBar.sendActionBar(player, this.PRIORITY, recharge.getFullProgressBar());
        }
    }
}