package me.trae.champions.weapon.weapons;

import me.trae.champions.weapon.WeaponManager;
import me.trae.champions.weapon.types.ChampionsPvPWeapon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BoosterAxe extends ChampionsPvPWeapon {

    public BoosterAxe(final WeaponManager manager) {
        super(manager, new ItemStack(Material.GOLD_AXE));
    }

    @Override
    public String getDisplayName() {
        return super.getDisplayName();
    }
}