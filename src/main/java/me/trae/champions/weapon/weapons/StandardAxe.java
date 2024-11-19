package me.trae.champions.weapon.weapons;

import me.trae.api.champions.weapon.ChampionsPvPWeapon;
import me.trae.champions.weapon.WeaponManager;
import me.trae.core.utility.UtilString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class StandardAxe extends ChampionsPvPWeapon {

    public StandardAxe(final WeaponManager manager) {
        super(manager, new ItemStack(Material.IRON_AXE));
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Just a Standard Axe.",
                "",
                UtilString.pair("<gray>Damage", String.format("<green>%s", this.getDamage()))
        };
    }

    @Override
    public double getDamage() {
        return 3.0D;
    }
}