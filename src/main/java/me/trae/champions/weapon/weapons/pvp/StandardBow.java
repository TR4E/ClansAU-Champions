package me.trae.champions.weapon.weapons.pvp;

import me.trae.champions.weapon.WeaponManager;
import me.trae.champions.weapon.types.ChampionsPvPWeapon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class StandardBow extends ChampionsPvPWeapon {

    public StandardBow(final WeaponManager manager) {
        super(manager, new ItemStack(Material.BOW));
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Just a Standard Bow."
        };
    }
}