package me.trae.champions.build.modules;

import me.trae.champions.Champions;
import me.trae.champions.build.BuildManager;
import me.trae.core.framework.types.frame.SpigotListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class HandleRemoveBuildDataOnPlayerQuit extends SpigotListener<Champions, BuildManager> {

    public HandleRemoveBuildDataOnPlayerQuit(final BuildManager manager) {
        super(manager);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.getManager().removeBuilds(event.getPlayer());
    }
}