package me.trae.champions.skill.modules;

import me.trae.api.champions.role.Role;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.recharge.Recharge;
import me.trae.core.recharge.events.RechargeUpdaterEvent;
import me.trae.core.utility.UtilAbility;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.injectors.annotations.Inject;
import me.trae.core.weapon.Weapon;
import me.trae.core.weapon.WeaponManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class HandleSkillRechargeProgressBar extends SpigotListener<Champions, SkillManager> {

    private final int PRIORITY = 4;

    @Inject
    private WeaponManager weaponManager;

    @Inject
    private RoleManager roleManager;

    public HandleSkillRechargeProgressBar(final SkillManager manager) {
        super(manager);
    }

    @EventHandler
    public void onRechargeUpdater(final RechargeUpdaterEvent event) {
        if (!(event.isProgressBar())) {
            return;
        }

        final Recharge recharge = event.getRecharge();

        if (!(recharge.isInform())) {
            return;
        }

        final Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        final ItemStack itemStack = player.getEquipment().getItemInHand();
        if (itemStack == null) {
            UtilAbility.removeActionBar(player, this.PRIORITY);
            return;
        }

        if (Arrays.stream(SkillType.values()).noneMatch(skillType -> skillType.isItemStack(itemStack))) {
            UtilAbility.removeActionBar(player, this.PRIORITY);
            return;
        }

        final Weapon<?, ?, ?> weapon = this.weaponManager.getWeaponByItemStack(itemStack);
        if (weapon != null && !(weapon.isChampionsWeapon())) {
            UtilAbility.removeActionBar(player, this.PRIORITY);
            return;
        }

        final Role playerRole = this.roleManager.getPlayerRole(player);
        if (playerRole == null) {
            UtilAbility.removeActionBar(player, this.PRIORITY);
            return;
        }

        final ActiveSkill<?, ?> skill = UtilJava.cast(ActiveSkill.class, playerRole.getSubModuleByName(recharge.getName()));
        if (skill == null) {
            return;
        }

        final int level = skill.getLevel(player);
        if (level == 0) {
            UtilAbility.removeActionBar(player, this.PRIORITY);
            return;
        }

        if (!(skill.hasRecharge(level))) {
            UtilAbility.removeActionBar(player, this.PRIORITY);
            return;
        }

        if (!(skill.getType().isItemStack(itemStack))) {
            return;
        }

        UtilAbility.sendActionBar(player, this.PRIORITY, recharge);
    }
}