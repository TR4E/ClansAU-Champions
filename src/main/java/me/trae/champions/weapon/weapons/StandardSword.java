package me.trae.champions.weapon.weapons;

import me.trae.api.champions.weapon.ChampionsPvPWeapon;
import me.trae.champions.weapon.WeaponManager;
import me.trae.core.utility.UtilString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class StandardSword extends ChampionsPvPWeapon {

    public StandardSword(final WeaponManager manager) {
        super(manager, new ItemStack(Material.IRON_SWORD));
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Just a Standard Sword.",
                "",
                UtilString.pair("<gray>Damage", String.format("<green>%s", this.getDamage()))
        };
    }

    @Override
    public double getDamage() {
        return 4.0D;
    }
}