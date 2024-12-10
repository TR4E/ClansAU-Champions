package me.trae.champions.world.modules;

import me.trae.api.damage.events.armour.ArmourDurabilityEvent;
import me.trae.champions.Champions;
import me.trae.champions.world.WorldManager;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.utility.enums.ArmourMaterialType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Arrays;

public class HandleChampionsArmourDurability extends SpigotListener<Champions, WorldManager> {

    public HandleChampionsArmourDurability(final WorldManager manager) {
        super(manager);
    }

    @EventHandler
    public void onArmourDurability(final ArmourDurabilityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(Arrays.asList(ArmourMaterialType.LEATHER, ArmourMaterialType.GOLD).contains(event.getMaterialType()))) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (Math.random() <= 0.52D) {
            return;
        }

        event.setCancelled(true);
    }
}