package me.trae.champions.weapon.weapons.pvp;

import me.trae.champions.weapon.WeaponManager;
import me.trae.champions.weapon.models.PassiveActivatorWeapon;
import me.trae.champions.weapon.types.ChampionsPvPWeapon;
import me.trae.core.utility.UtilString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class StandardAxe extends ChampionsPvPWeapon implements PassiveActivatorWeapon {

    public StandardAxe(final WeaponManager manager) {
        super(manager, new ItemStack(Material.IRON_AXE));
    }

    @Override
    public int getModel() {
        return 708266;
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