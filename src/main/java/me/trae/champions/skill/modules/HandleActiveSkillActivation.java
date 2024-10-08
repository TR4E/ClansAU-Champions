package me.trae.champions.skill.modules;

import me.trae.api.champions.skill.SkillActivateEvent;
import me.trae.api.champions.skill.SkillPreActivateEvent;
import me.trae.champions.Champions;
import me.trae.champions.role.Role;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.utility.UtilServer;
import me.trae.core.world.events.PlayerItemInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class HandleActiveSkillActivation extends SpigotListener<Champions, SkillManager> {

    public HandleActiveSkillActivation(final SkillManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemInteract(final PlayerItemInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        final Role role = this.getInstance().getManagerByClass(RoleManager.class).getPlayerRole(player);
        if (role == null) {
            return;
        }

        final SkillType skillType = SkillType.getByMaterial(event.getItemStack().getType());
        if (skillType == null) {
            return;
        }

        if (!(skillType.getActionType().isAction(event.getAction()))) {
            return;
        }

        final ActiveSkill<?, ?> skill = role.getSkillByType(ActiveSkill.class, skillType);
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

        if (!(this.canActivate(player, skill))) {
            return;
        }

        final SkillActivateEvent activateEvent = new SkillActivateEvent(skill, player);
        UtilServer.callEvent(activateEvent);
        if (activateEvent.isCancelled()) {
            return;
        }

        skill.onActivate(player, level);
    }

    private boolean canActivate(final Player player, final ActiveSkill<?, ?> skill) {
        if (!(skill.canActivate(player))) {
            return false;
        }

        return true;
    }
}