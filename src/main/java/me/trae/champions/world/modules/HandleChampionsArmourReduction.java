package me.trae.champions.world.modules;

import me.trae.api.damage.events.armour.ArmourReductionEvent;
import me.trae.champions.Champions;
import me.trae.champions.world.WorldManager;
import me.trae.core.framework.types.frame.SpigotListener;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

public class HandleChampionsArmourReduction extends SpigotListener<Champions, WorldManager> {

    public HandleChampionsArmourReduction(final WorldManager manager) {
        super(manager);
    }

    @EventHandler
    public void onArmourReduction(final ArmourReductionEvent event) {
        if (event.isCancelled()) {
            return;
        }

        event.setReduction(this.getValueByMaterial(event.getItemStack().getType()));
    }

    private double getValueByMaterial(final Material material) {
        switch (material) {
            case DIAMOND_HELMET:
            case LEATHER_LEGGINGS:
                return 10.0D;

            case IRON_HELMET:
            case CHAINMAIL_HELMET:
            case GOLD_HELMET:
            case IRON_BOOTS:
            case GOLD_BOOTS:
                return 8.0D;

            case LEATHER_HELMET:
            case LEATHER_BOOTS:
                return 6.0D;

            case DIAMOND_CHESTPLATE:
            case IRON_CHESTPLATE:
                return 24.0D;

            case GOLD_CHESTPLATE:
                return 22.0D;

            case CHAINMAIL_CHESTPLATE:
            case IRON_LEGGINGS:
            case GOLD_LEGGINGS:
                return 20.0D;

            case LEATHER_CHESTPLATE:
                return 14.0D;

            case DIAMOND_LEGGINGS:
                return 18.0D;

            case CHAINMAIL_LEGGINGS:
                return 16.0D;

            case DIAMOND_BOOTS:
                return 12.0D;

            case CHAINMAIL_BOOTS:
                return 1.8D;
        }

        return 0.0D;
    }
}