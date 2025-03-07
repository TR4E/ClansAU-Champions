package me.trae.champions.weapon.weapons.pvp;

import me.trae.champions.weapon.WeaponManager;
import me.trae.champions.weapon.models.PassiveActivatorWeapon;
import me.trae.champions.weapon.types.ChampionsPvPWeapon;
import me.trae.core.utility.UtilString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PowerAxe extends ChampionsPvPWeapon implements PassiveActivatorWeapon {

    public PowerAxe(final WeaponManager manager) {
        super(manager, new ItemStack(Material.DIAMOND_AXE));
    }

    @Override
    public int getModel() {
        return 372171;
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Increases Melee Damage by 1.",
                "",
                UtilString.pair("<gray>Damage", UtilString.format("<green>%s", this.getDamage()))
        };
    }

    @Override
    public double getDamage() {
        return 5.0D;
    }
}