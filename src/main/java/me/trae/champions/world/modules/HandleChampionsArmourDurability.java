package me.trae.champions.world.modules;

import me.trae.api.damage.events.armour.ArmourDurabilityEvent;
import me.trae.champions.Champions;
import me.trae.champions.world.WorldManager;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.utility.UtilMath;
import me.trae.core.utility.enums.ArmourMaterialType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class HandleChampionsArmourDurability extends SpigotListener<Champions, WorldManager> {

    public HandleChampionsArmourDurability(final WorldManager manager) {
        super(manager);
    }

    @EventHandler
    public void onArmourDurability(final ArmourDurabilityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (this.canTakeDurability(event.getMaterialType())) {
            return;
        }

        event.setCancelled(true);
    }

    private boolean canTakeDurability(final ArmourMaterialType materialType) {
        switch (materialType) {
            case LEATHER: {
                if (UtilMath.getRandomNumber(Integer.class, 1, 7) == 6) {
                    return true;
                }
                break;
            }
            case CHAINMAIL:
            case IRON: {
                if (UtilMath.getRandomNumber(Integer.class, 0, 10) >= 5) {
                    return true;
                }
                break;
            }
            case GOLD: {
                if (UtilMath.getRandomNumber(Integer.class, 1, 6) == 5) {
                    return true;
                }
                break;
            }
            default: {
                return true;
            }
        }

        return false;
    }
}