package me.trae.champions.weapon.weapons.pvp;

import me.trae.champions.weapon.WeaponManager;
import me.trae.champions.weapon.models.PassiveActivatorWeapon;
import me.trae.champions.weapon.types.ChampionsPvPWeapon;
import me.trae.core.utility.UtilString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PowerSword extends ChampionsPvPWeapon implements PassiveActivatorWeapon {

    public PowerSword(final WeaponManager manager) {
        super(manager, new ItemStack(Material.DIAMOND_SWORD));
    }

    @Override
    public int getModel() {
        return 674655;
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