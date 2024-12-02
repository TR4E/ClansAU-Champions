package me.trae.champions.world.modules;

import me.trae.api.damage.events.damage.CustomPreDamageEvent;
import me.trae.api.damage.events.weapon.WeaponReductionEvent;
import me.trae.champions.Champions;
import me.trae.champions.world.WorldManager;
import me.trae.core.framework.types.frame.SpigotListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

public class HandleChampionsWeaponReduction extends SpigotListener<Champions, WorldManager> {

    public HandleChampionsWeaponReduction(final WorldManager manager) {
        super(manager);
    }

    @EventHandler
    public void onWeaponReduction(final WeaponReductionEvent event) {
        if (event.isCancelled()) {
            return;
        }

        switch (event.getItemStack().getType()) {
            case STONE_SWORD:
                event.setReduction(3.0D);
                break;
            case STONE_AXE:
            case WOOD_SWORD:
                event.setReduction(2.0D);
                break;
            case WOOD_AXE:
                event.setReduction(1.0D);
                break;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCustomPreDamage(final CustomPreDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        event.setDamage(1.0D);
    }
}