package me.trae.champions.build.modules;

import me.trae.champions.Champions;
import me.trae.champions.build.BuildManager;
import me.trae.core.Core;
import me.trae.core.client.ClientManager;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.item.events.ItemUpdateEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class HandleClassCustomizationTable extends SpigotListener<Champions, BuildManager> {

    @ConfigInject(type = String.class, path = "Material", defaultValue = "ENCHANTMENT_TABLE")
    private String material;

    public HandleClassCustomizationTable(final BuildManager manager) {
        super(manager);
    }

    private Material getMaterial() {
        try {
            return Material.valueOf(this.material);
        } catch (final Exception ignored) {
        }

        return Material.ENCHANTMENT_TABLE;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final Block block = event.getClickedBlock();

        if (block.getType() != this.getMaterial()) {
            return;
        }

        final Player player = event.getPlayer();

        if (this.getInstanceByClass(Core.class).getManagerByClass(ClientManager.class).getClientByPlayer(player).isAdministrating()) {
            return;
        }

        event.setCancelled(true);

        this.getManager().openMenu(player);
    }

    @EventHandler
    public void onItemUpdate(final ItemUpdateEvent event) {
        if (event.getBuilder().getItemStack().getType() != this.getMaterial()) {
            return;
        }

        event.getBuilder().setDisplayName("Class Customization");
    }
}