package me.trae.champions.weapon.weapons;

import me.trae.champions.weapon.WeaponManager;
import me.trae.champions.weapon.types.ChampionsPvPWeapon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BoosterSword extends ChampionsPvPWeapon {

    public BoosterSword(final WeaponManager manager) {
        super(manager, new ItemStack(Material.GOLD_SWORD));
    }
}