package me.trae.champions.role.modules.damage;

import me.trae.api.champions.role.Role;
import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.core.framework.types.frame.SpigotListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class HandleRoleCustomDamageSound extends SpigotListener<Champions, RoleManager> {

    public HandleRoleCustomDamageSound(final RoleManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCustomDamage(final CustomPostDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamagee() instanceof Player)) {
            return;
        }

        final Role role = this.getManager().getPlayerRole(event.getDamageeByClass(Player.class));
        if (role == null) {
            return;
        }

        event.setSoundCreator(role.getDamageSound());
    }
}