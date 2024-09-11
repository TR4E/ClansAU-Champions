package me.trae.champions.weapon.types;

import me.trae.champions.Champions;
import me.trae.champions.weapon.WeaponManager;
import me.trae.core.weapon.data.WeaponData;
import me.trae.core.weapon.types.CustomItem;
import org.bukkit.inventory.ItemStack;

public abstract class ChampionsPvPWeapon extends CustomItem<Champions, WeaponManager, WeaponData> {

    public ChampionsPvPWeapon(final WeaponManager manager, final ItemStack itemStack) {
        super(manager, itemStack);
    }

    @Override
    public Class<WeaponData> getClassOfData() {
        return WeaponData.class;
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