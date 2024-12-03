package me.trae.champions.world.modules;

import me.trae.api.damage.events.weapon.WeaponDurabilityEvent;
import me.trae.champions.Champions;
import me.trae.champions.world.WorldManager;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.utility.enums.WeaponMaterialType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class HandleChampionsWeaponDurability extends SpigotListener<Champions, WorldManager> {

    public HandleChampionsWeaponDurability(final WorldManager manager) {
        super(manager);
    }

    @EventHandler
    public void onWeaponDurability(final WeaponDurabilityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getMaterialType() != WeaponMaterialType.GOLD) {
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