package me.trae.champions.world.modules;

import me.trae.champions.Champions;
import me.trae.champions.world.WorldManager;
import me.trae.core.Core;
import me.trae.core.client.ClientManager;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.utility.UtilBlock;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.UtilMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;

public class DisableShootingArrowsWhileInLiquid extends SpigotListener<Champions, WorldManager> {

    public DisableShootingArrowsWhileInLiquid(final WorldManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityShootBow(final EntityShootBowEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = UtilJava.cast(Player.class, event.getEntity());

        if (!(UtilBlock.isInLiquid(player.getLocation()))) {
            return;
        }

        if (this.getInstance(Core.class).getManagerByClass(ClientManager.class).getClientByPlayer(player).isAdministrating()) {
            return;
        }

        event.setCancelled(true);

        player.updateInventory();

        UtilMessage.message(player, "Restrictions", "You cannot shoot arrows while in liquid!");
    }
}