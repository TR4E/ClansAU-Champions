package me.trae.champions.weapon.weapons;

import me.trae.champions.weapon.WeaponManager;
import me.trae.champions.weapon.types.ChampionsPvPWeapon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PowerSword extends ChampionsPvPWeapon {

    public PowerSword(final WeaponManager manager) {
        super(manager, new ItemStack(Material.DIAMOND_SWORD));
    }
}