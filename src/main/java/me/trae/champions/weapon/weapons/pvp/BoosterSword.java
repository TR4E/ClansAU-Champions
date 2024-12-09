package me.trae.champions.weapon.weapons.pvp;

import me.trae.champions.weapon.WeaponManager;
import me.trae.champions.weapon.models.BoosterWeapon;
import me.trae.champions.weapon.models.PassiveActivatorWeapon;
import me.trae.champions.weapon.types.ChampionsPvPWeapon;
import me.trae.core.utility.UtilString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BoosterSword extends ChampionsPvPWeapon implements BoosterWeapon, PassiveActivatorWeapon {

    public BoosterSword(final WeaponManager manager) {
        super(manager, new ItemStack(Material.GOLD_SWORD));
    }

    @Override
    public int getModel() {
        return 992916;
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Increases Sword Skill Level by 1.",
                "",
                UtilString.pair("<gray>Damage", String.format("<green>%s", this.getDamage()))
        };
    }

    @Override
    public double getDamage() {
        return 5.0D;
    }
}