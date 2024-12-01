package me.trae.champions.role.modules.durability;

import me.trae.api.damage.events.armour.ArmourDurabilityEvent;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.utility.enums.ArmourMaterialType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class HandleMageArmourDurability extends SpigotListener<Champions, RoleManager> {

    public HandleMageArmourDurability(final RoleManager manager) {
        super(manager);
    }

    @EventHandler
    public void onArmourDurability(final ArmourDurabilityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getMaterialType() != ArmourMaterialType.GOLD) {
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