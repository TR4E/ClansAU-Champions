package me.trae.champions.weapon.weapons.pvp;

import me.trae.champions.weapon.WeaponManager;
import me.trae.champions.weapon.models.BoosterWeapon;
import me.trae.champions.weapon.types.ChampionsPvPWeapon;
import me.trae.core.utility.UtilString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BoosterAxe extends ChampionsPvPWeapon implements BoosterWeapon {

    public BoosterAxe(final WeaponManager manager) {
        super(manager, new ItemStack(Material.GOLD_AXE));
    }

    @Override
    public int getModel() {
        return 545759;
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Increases Axe Skill Level by 1.",
                "",
                UtilString.pair("<gray>Damage", String.format("<green>%s", this.getDamage()))
        };
    }

    @Override
    public double getDamage() {
        return 4.0D;
    }
}