package me.trae.champions.skill.modules;

import me.trae.champions.Champions;
import me.trae.api.champions.role.Role;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.recharge.Recharge;
import me.trae.core.recharge.events.RechargeUpdaterEvent;
import me.trae.core.utility.UtilServer;
import me.trae.core.utility.UtilTitle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class HandleSkillRechargeProgressBar extends SpigotListener<Champions, SkillManager> {

    private final String ACTION_BAR_KEY = "Skill Recharge Progress Bar";

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
            this.reset(player);
            return;
        }

        final Role playerRole = this.getInstance().getManagerByClass(RoleManager.class).getPlayerRole(player);
        if (playerRole == null) {
            this.reset(player);
            return;
        }

        for (final ActiveSkill<?, ?> skill : playerRole.getSkillsByClass(ActiveSkill.class)) {
            if (!(skill.getName().equals(recharge.getName()))) {
                continue;
            }

            if (!(skill.hasRecharge(skill.getLevel(player)))) {
                continue;
            }

            if (!(skill.getType().isItemStack(itemStack))) {
                this.reset(player);
                continue;
            }

            this.sendActionBar(player, recharge);
        }
    }

    private void sendActionBar(final Player player, final Recharge recharge) {
        if (recharge.hasExpired()) {
            UtilServer.runTaskLater(Champions.class, false, 20L, () -> UtilTitle.sendActionBarByLock(player, " ", this.ACTION_BAR_KEY));
        }

        UtilTitle.sendActionBarByKey(player, recharge.getFullProgressBar(), this.ACTION_BAR_KEY);
    }

    private void reset(final Player player) {
        if (!(UtilTitle.isActionBarByKey(player, this.ACTION_BAR_KEY))) {
            return;
        }

        UtilTitle.sendActionBarByKey(player, " ", this.ACTION_BAR_KEY);
    }
}