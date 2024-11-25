package me.trae.champions.role.modules;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.role.events.RoleChangeEvent;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.champions.utility.UtilRole;
import me.trae.core.framework.types.frame.SpigotUpdater;
import me.trae.core.updater.annotations.Update;
import me.trae.core.utility.UtilServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HandleRoleEquip extends SpigotUpdater<Champions, RoleManager> {

    public HandleRoleEquip(final RoleManager manager) {
        super(manager);
    }

    @Update(delay = 125L)
    public void onUpdater() {
        for (final Player player : UtilServer.getOnlinePlayers()) {
            Role playerRole = null;

            for (final Role role : this.getManager().getModulesByClass(Role.class)) {
                if (!(role.hasArmour(player))) {
                    continue;
                }

                playerRole = role;
                break;
            }

            this.equip(player, playerRole);
        }

        this.getManager().getPlayerRoles().keySet().removeIf(uuid -> Bukkit.getPlayer(uuid) == null);
    }

    private void equip(final Player player, final Role role) {
        final Role oldRole = this.getManager().getPlayerRole(player);

        if (this.getManager().hasPlayerRole(player) && oldRole == role) {
            return;
        }

        if (oldRole != null) {
            oldRole.reset(player);
        }

        this.getManager().setPlayerRole(player, role);

        UtilRole.equipRoleEffect(role, player, true, false);

        UtilServer.callEvent(new RoleChangeEvent(role, player));
    }
}