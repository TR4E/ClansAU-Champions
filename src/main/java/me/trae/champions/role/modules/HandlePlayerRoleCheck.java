package me.trae.champions.role.modules;

import me.trae.api.champions.role.events.RoleCheckEvent;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.core.framework.types.frame.SpigotListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class HandlePlayerRoleCheck extends SpigotListener<Champions, RoleManager> {

    public HandlePlayerRoleCheck(final RoleManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRoleCheck(final RoleCheckEvent event) {
        event.setRole(this.getManager().getPlayerRole(event.getPlayer()));
    }
}