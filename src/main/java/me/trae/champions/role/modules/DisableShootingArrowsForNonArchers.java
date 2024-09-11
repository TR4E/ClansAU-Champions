package me.trae.champions.role.modules;

import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.champions.role.types.models.Archer;
import me.trae.core.Core;
import me.trae.core.client.ClientManager;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.UtilMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;

public class DisableShootingArrowsForNonArchers extends SpigotListener<Champions, RoleManager> {

    public DisableShootingArrowsForNonArchers(final RoleManager manager) {
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

        if (this.getManager().getPlayerRole(player) instanceof Archer) {
            return;
        }

        if (this.getInstance(Core.class).getManagerByClass(ClientManager.class).getClientByPlayer(player).isAdministrating()) {
            return;
        }

        event.setCancelled(true);

        player.updateInventory();

        UtilMessage.message(player, "Restrictions", "You must be an Archer Class to shoot arrows!");
    }
}