package me.trae.champions.weapon.weapons.pvp;

import me.trae.champions.weapon.WeaponManager;
import me.trae.champions.weapon.models.BoosterWeapon;
import me.trae.champions.weapon.types.ChampionsPvPWeapon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BoosterBow extends ChampionsPvPWeapon implements BoosterWeapon {

    public BoosterBow(final WeaponManager manager) {
        super(manager, new ItemStack(Material.BOW));
    }

    @Override
    public boolean isNaturallyObtained() {
        return false;
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Increases Bow Skill Level by 1.",
        };
    }
}