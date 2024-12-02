package me.trae.champions.weapon.types;

import me.trae.api.damage.events.weapon.WeaponReductionEvent;
import me.trae.champions.Champions;
import me.trae.champions.weapon.WeaponManager;
import me.trae.champions.weapon.types.interfaces.IChampionsPvPWeapon;
import me.trae.core.weapon.Weapon;
import me.trae.core.weapon.data.WeaponData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class ChampionsPvPWeapon extends Weapon<Champions, WeaponManager, WeaponData> implements IChampionsPvPWeapon, Listener {

    public ChampionsPvPWeapon(final WeaponManager manager, final ItemStack itemStack) {
        super(manager, itemStack);
    }

    @Override
    public Class<WeaponData> getClassOfData() {
        return WeaponData.class;
    }

    @Override
    public boolean isChampionsWeapon() {
        return true;
    }

    @Override
    public boolean isNaturallyObtained() {
        return true;
    }

    @Override
    public boolean showInMenu() {
        return false;
    }

    @Override
    public double getDamage() {
        return 0.0D;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWeaponReduction(final WeaponReductionEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(this.isWeaponByItemStack(event.getItemStack()))) {
            return;
        }

        event.setReduction(this.getDamage());
    }
}