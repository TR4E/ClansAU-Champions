package me.trae.champions.weapon.weapons.pvp;

import me.trae.champions.weapon.WeaponManager;
import me.trae.champions.weapon.models.PassiveActivatorWeapon;
import me.trae.champions.weapon.types.ChampionsPvPWeapon;
import me.trae.core.utility.UtilString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class StandardSword extends ChampionsPvPWeapon implements PassiveActivatorWeapon {

    public StandardSword(final WeaponManager manager) {
        super(manager, new ItemStack(Material.IRON_SWORD));
    }

    @Override
    public int getModel() {
        return 751114;
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Just a Standard Sword.",
                "",
                UtilString.pair("<gray>Damage", UtilString.format("<green>%s", this.getDamage()))
        };
    }

    @Override
    public double getDamage() {
        return 4.0D;
    }
}