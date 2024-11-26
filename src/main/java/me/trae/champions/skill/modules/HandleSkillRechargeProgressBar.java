package me.trae.champions.skill.modules;

import me.trae.api.champions.role.Role;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.core.Core;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.recharge.Recharge;
import me.trae.core.recharge.events.RechargeUpdaterEvent;
import me.trae.core.utility.UtilAbility;
import me.trae.core.weapon.Weapon;
import me.trae.core.weapon.WeaponManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class HandleSkillRechargeProgressBar extends SpigotListener<Champions, SkillManager> {

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
            UtilAbility.removeActionBar(player, "Skill", recharge);
            return;
        }

        final Weapon<?, ?, ?> weapon = this.getInstance(Core.class).getManagerByClass(WeaponManager.class).getWeaponByItemStack(itemStack);
        if (weapon != null && !(weapon.isChampionsWeapon())) {
            UtilAbility.removeActionBar(player, "Skill", recharge);
            return;
        }

        final Role playerRole = this.getInstance().getManagerByClass(RoleManager.class).getPlayerRole(player);
        if (playerRole == null) {
            UtilAbility.removeActionBar(player, "Skill", recharge);
            return;
        }

        for (final ActiveSkill<?, ?> skill : playerRole.getSkillsByClass(ActiveSkill.class)) {
            if (!(skill.getName().equals(recharge.getName()))) {
                continue;
            }

            final int level = skill.getLevel(player);
            if (level == 0) {
                UtilAbility.removeActionBar(player, "Skill", recharge);
                continue;
            }

            if (!(skill.hasRecharge(level))) {
                UtilAbility.removeActionBar(player, "Skill", recharge);
                continue;
            }

            if (!(skill.getType().isItemStack(itemStack))) {
                UtilAbility.removeActionBar(player, "Skill", recharge);
                continue;
            }

            UtilAbility.sendActionBar(player, "Skill", recharge);
            break;
        }
    }
}