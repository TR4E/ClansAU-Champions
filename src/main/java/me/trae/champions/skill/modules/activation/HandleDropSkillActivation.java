package me.trae.champions.skill.modules.activation;

import me.trae.api.champions.skill.events.SkillActivateEvent;
import me.trae.api.champions.skill.events.SkillPreActivateEvent;
import me.trae.champions.Champions;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.DropSkill;
import me.trae.champions.weapon.models.PassiveActivatorWeapon;
import me.trae.core.Core;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.utility.UtilServer;
import me.trae.core.weapon.WeaponManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class HandleDropSkillActivation extends SpigotListener<Champions, SkillManager> {

    public HandleDropSkillActivation(final SkillManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getItemDrop() == null) {
            return;
        }

        final ItemStack itemStack = event.getItemDrop().getItemStack();

        if (!(this.getInstance(Core.class).getManagerByClass(WeaponManager.class).getWeaponByItemStack(itemStack) instanceof PassiveActivatorWeapon)) {
            return;
        }

        final Player player = event.getPlayer();

        final DropSkill<?, ?> skill = this.getManager().getSkillByType(DropSkill.class, player, SkillType.PASSIVE_B);
        if (skill == null) {
            return;
        }

        event.setCancelled(true);

        final int level = skill.getLevel(player);
        if (level == 0) {
            return;
        }

        final SkillPreActivateEvent preActivateEvent = new SkillPreActivateEvent(skill, player);
        UtilServer.callEvent(preActivateEvent);
        if (preActivateEvent.isCancelled()) {
            return;
        }

        if (!(this.getManager().canActivateActiveSkill(player, skill))) {
            return;
        }

        final SkillActivateEvent activateEvent = new SkillActivateEvent(skill, player);
        UtilServer.callEvent(activateEvent);
        if (activateEvent.isCancelled()) {
            return;
        }

        skill.onActivate(player, level);
    }
}