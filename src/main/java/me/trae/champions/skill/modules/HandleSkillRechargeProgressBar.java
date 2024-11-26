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
import me.trae.core.utility.UtilServer;
import me.trae.core.utility.UtilTitle;
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
            this.reset(player, recharge);
            return;
        }

        final Role playerRole = this.getInstance().getManagerByClass(RoleManager.class).getPlayerRole(player);
        if (playerRole == null) {
            this.reset(player, recharge);
            return;
        }

        for (final ActiveSkill<?, ?> skill : playerRole.getSkillsByClass(ActiveSkill.class)) {
            if (!(skill.getName().equals(recharge.getName()))) {
                continue;
            }

            final int level = skill.getLevel(player);
            if (level == 0) {
                continue;
            }

            if (!(skill.hasRecharge(level))) {
                continue;
            }

            if (!(skill.getType().isItemStack(itemStack))) {
                this.reset(player, recharge);
                continue;
            }

            this.sendActionBar(player, recharge);
            break;
        }
    }

    private void sendActionBar(final Player player, final Recharge recharge) {
        if (recharge.hasExpired()) {
            UtilServer.runTaskLater(Core.class, false, 20L, () -> this.reset(player, recharge));
        }

        UtilTitle.sendActionBarByLock(player, recharge.getFullProgressBar(), recharge.getName());
    }

    private void reset(final Player player, final Recharge recharge) {
        UtilTitle.sendActionBarByLock(player, " ", recharge.getName());
    }
}