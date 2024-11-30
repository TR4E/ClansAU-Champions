package me.trae.champions.role.modules.reset;

import me.trae.api.champions.role.Role;
import me.trae.api.death.events.CustomDeathEvent;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.core.framework.types.frame.SpigotListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class ResetPlayerRoleOnPlayerDeath extends SpigotListener<Champions, RoleManager> {

    public ResetPlayerRoleOnPlayerDeath(final RoleManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCustomDeath(final CustomDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = event.getEntityByClass(Player.class);

        final Role playerRole = this.getManager().getPlayerRole(player);
        if (playerRole == null) {
            return;
        }

        playerRole.reset(player);
    }
}