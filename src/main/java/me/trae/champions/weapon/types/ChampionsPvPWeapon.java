package me.trae.champions.weapon.types;

import me.trae.champions.Champions;
import me.trae.champions.weapon.WeaponManager;
import me.trae.core.weapon.Weapon;
import org.bukkit.inventory.ItemStack;

public abstract class ChampionsPvPWeapon extends Weapon<Champions, WeaponManager> {

    public ChampionsPvPWeapon(final WeaponManager manager, final ItemStack itemStack) {
        super(manager, itemStack);
    }

    @Override
    public String getDisplayName() {
        return String.format("<gold>%s", this.getName());
    }

    @Override
    public boolean isNaturallyObtained() {
        return true;
    }
}