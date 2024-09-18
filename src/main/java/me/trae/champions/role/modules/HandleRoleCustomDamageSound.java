package me.trae.champions.role.modules;

import me.trae.api.damage.events.CustomDamageEvent;
import me.trae.champions.Champions;
import me.trae.champions.role.Role;
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
    public void onCustomDamage(final CustomDamageEvent event) {
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