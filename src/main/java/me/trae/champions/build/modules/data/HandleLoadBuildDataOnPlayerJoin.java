package me.trae.champions.build.modules.data;

import me.trae.api.champions.role.Role;
import me.trae.champions.Champions;
import me.trae.champions.build.BuildManager;
import me.trae.champions.build.data.types.DefaultRoleBuild;
import me.trae.champions.role.RoleManager;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.utility.UtilServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class HandleLoadBuildDataOnPlayerJoin extends SpigotListener<Champions, BuildManager> {

    public HandleLoadBuildDataOnPlayerJoin(final BuildManager manager) {
        super(manager);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        this.getManager().getRepository().loadData(player.getUniqueId());

        UtilServer.runTaskLater(Champions.class, false, 10L, () -> {
            for (final Role role : this.getInstance().getManagerByClass(RoleManager.class).getModulesByClass(Role.class)) {
                this.getManager().addBuild(new DefaultRoleBuild(player, role));
            }
        });
    }
}