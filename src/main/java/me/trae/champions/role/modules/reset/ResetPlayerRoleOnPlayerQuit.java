package me.trae.champions.role.modules.reset;

import me.trae.api.champions.role.Role;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.core.framework.types.frame.SpigotListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

public class ResetPlayerRoleOnPlayerQuit extends SpigotListener<Champions, RoleManager> {

    public ResetPlayerRoleOnPlayerQuit(final RoleManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        final Role playerRole = this.getManager().getPlayerRole(player);
        if (playerRole == null) {
            return;
        }

        playerRole.reset(player);
    }
}