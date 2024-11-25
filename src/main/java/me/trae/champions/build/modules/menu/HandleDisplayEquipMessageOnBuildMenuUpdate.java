package me.trae.champions.build.modules.menu;

import me.trae.api.champions.role.Role;
import me.trae.champions.Champions;
import me.trae.champions.build.BuildManager;
import me.trae.champions.build.menus.build.interfaces.IBuildMenu;
import me.trae.champions.role.RoleManager;
import me.trae.champions.utility.UtilRole;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.menu.events.MenuCloseEvent;
import me.trae.core.utility.UtilJava;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class HandleDisplayEquipMessageOnBuildMenuUpdate extends SpigotListener<Champions, BuildManager> {

    public HandleDisplayEquipMessageOnBuildMenuUpdate(final BuildManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMenuClose(final MenuCloseEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getMenu() instanceof IBuildMenu)) {
            return;
        }

        final IBuildMenu buildMenu = UtilJava.cast(IBuildMenu.class, event.getMenu());

        final Role updatedRole = buildMenu.getUpdatedRole();

        final Player player = event.getPlayer();

        final Role playerRole = this.getInstance().getManagerByClass(RoleManager.class).getPlayerRole(player);
        if (playerRole == null) {
            return;
        }

        if (updatedRole == null || updatedRole != playerRole) {
            return;
        }

        UtilRole.equipRoleEffect(updatedRole, player, false, true);
    }
}