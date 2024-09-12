package me.trae.champions.weapon.weapons;

import me.trae.champions.weapon.WeaponManager;
import me.trae.champions.weapon.models.BoosterWeapon;
import me.trae.champions.weapon.types.ChampionsPvPWeapon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BoosterAxe extends ChampionsPvPWeapon implements BoosterWeapon {

    public BoosterAxe(final WeaponManager manager) {
        super(manager, new ItemStack(Material.GOLD_AXE));
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Increases Skill Level by 1."
        };
    }
}