package me.trae.champions.role.interfaces;

import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IRole {

    String getPrefix();

    String[] getDescription();

    void reset(final Player player);

    List<String> getEquipMessage();

    List<Material> getArmour();

    default boolean hasArmour(final Player player) {
        for (final ItemStack itemStack : player.getEquipment().getArmorContents()) {
            if (itemStack != null && this.getArmour().contains(itemStack.getType())) {
                continue;
            }

            return false;
        }

        return true;
    }

    SoundCreator getDamageSound();
}