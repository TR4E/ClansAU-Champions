package me.trae.champions.role.modules;

import me.trae.api.death.events.CustomDeathMessageEvent;
import me.trae.champions.Champions;
import me.trae.champions.role.Role;
import me.trae.champions.role.RoleManager;
import me.trae.core.framework.types.frame.SpigotListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class HandleChampionsDeathMessageFormat extends SpigotListener<Champions, RoleManager> {

    public HandleChampionsDeathMessageFormat(final RoleManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCustomDeathMessage(final CustomDeathMessageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getDeathEvent().getEntity() instanceof Player) {
            event.setEntityName(this.getPlayerName(event.getDeathEvent().getEntityByClass(Player.class), event.getEntityName()));
        }

        if (event.getDeathEvent().getKiller() instanceof Player) {
            event.setKillerName(this.getPlayerName(event.getDeathEvent().getKillerByClass(Player.class), event.getKillerName()));
        }
    }

    private String getPlayerName(final Player player, final String playerName) {
        final Role role = this.getManager().getPlayerRole(player);
        if (role == null) {
            return playerName;
        }

        return String.format("<green>%s<white>.%s", role.getPrefix(), playerName);
    }
}