package me.trae.champions.role.modules;

import me.trae.champions.Champions;
import me.trae.api.champions.role.Role;
import me.trae.champions.role.RoleManager;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.player.events.PlayerDisplayNameEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class HandleChampionsPlayerDisplayNameFormat extends SpigotListener<Champions, RoleManager> {

    public HandleChampionsPlayerDisplayNameFormat(final RoleManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDisplayName(final PlayerDisplayNameEvent event) {
        if (!(event.isExtra())) {
            return;
        }

        final Player player = event.getPlayer();

        final Role role = this.getManager().getPlayerRole(player);
        if (role == null) {
            return;
        }

        event.setPlayerName(String.format("<green>%s<white>.%s", role.getPrefix(), event.getPlayerName()));
    }
}