package me.trae.champions.build.modules;

import me.trae.champions.Champions;
import me.trae.champions.build.BuildManager;
import me.trae.core.framework.types.frame.SpigotListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class HandleLoadBuildDataOnPlayerJoin extends SpigotListener<Champions, BuildManager> {

    public HandleLoadBuildDataOnPlayerJoin(final BuildManager manager) {
        super(manager);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.getManager().getRepository().loadData(event.getPlayer().getUniqueId());
    }
}