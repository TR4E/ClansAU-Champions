package me.trae.champions.build.modules.data;

import me.trae.champions.Champions;
import me.trae.champions.build.BuildManager;
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

        UtilServer.runTaskLater(Champions.class, false, 10L, () -> this.getManager().fixRoleBuild(player));
    }
}