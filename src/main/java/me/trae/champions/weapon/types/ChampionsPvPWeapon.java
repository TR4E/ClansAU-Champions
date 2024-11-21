package me.trae.champions.weapon.types;

import me.trae.api.damage.events.CustomDamageEvent;
import me.trae.champions.Champions;
import me.trae.champions.weapon.WeaponManager;
import me.trae.champions.weapon.types.interfaces.IChampionsPvPWeapon;
import me.trae.core.weapon.Weapon;
import me.trae.core.weapon.data.WeaponData;
import org.bukkit.ChatColor;
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
    public String getDisplayName() {
        return ChatColor.GOLD + this.getName();
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

    @EventHandler(priority = EventPriority.LOW)
    public void onCustomDamage(final CustomDamageEvent event) {
        if (this.getDamage() <= 0.0D) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (!(this.isWeaponByItemStack(event.getDamagerByClass(Player.class).getInventory().getItemInHand()))) {
            return;
        }

        event.setDamage(this.getDamage());
    }
}