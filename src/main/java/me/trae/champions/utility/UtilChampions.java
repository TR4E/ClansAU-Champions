package me.trae.champions.utility;

import me.trae.champions.weapon.models.BoosterWeapon;
import me.trae.core.Core;
import me.trae.core.utility.UtilPlugin;
import me.trae.core.weapon.WeaponManager;
import org.bukkit.inventory.ItemStack;

public class UtilChampions {

    public static boolean isBoosterWeapon(final ItemStack itemStack) {
        return UtilPlugin.getInstance(Core.class).getManagerByClass(WeaponManager.class).getWeaponByItemStack(itemStack) instanceof BoosterWeapon;
    }
}