package me.trae.champions.weapon.weapons;

import me.trae.api.champions.weapon.ChampionsPvPWeapon;
import me.trae.champions.weapon.WeaponManager;
import me.trae.core.utility.UtilString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PowerSword extends ChampionsPvPWeapon {

    public PowerSword(final WeaponManager manager) {
        super(manager, new ItemStack(Material.DIAMOND_SWORD));
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Increases Melee Damage by 1.",
                "",
                UtilString.pair("<gray>Damage", String.format("<green>%s", this.getDamage()))
        };
    }

    @Override
    public double getDamage() {
        return 6.0D;
    }
}