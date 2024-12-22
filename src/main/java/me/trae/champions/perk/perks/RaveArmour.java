package me.trae.champions.perk.perks;

import me.trae.champions.Champions;
import me.trae.champions.perk.PerkManager;
import me.trae.champions.preference.PreferenceManager;
import me.trae.champions.preference.types.DisplayRaveArmour;
import me.trae.champions.role.RoleManager;
import me.trae.champions.role.types.Assassin;
import me.trae.core.perk.Perk;
import me.trae.core.updater.annotations.Update;
import me.trae.core.updater.interfaces.Updater;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.enums.ArmourMaterialType;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;

public class RaveArmour extends Perk<Champions, PerkManager> implements Updater {

    private Assassin ASSASSIN_ROLE;
    private DisplayRaveArmour PREFERENCE;

    private int count;

    public RaveArmour(final PerkManager manager) {
        super(manager);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Dynamic assassin leather armour.",
                "Cycles dye colors through every hue."
        };
    }

    @Override
    public void onInitialize() {
        this.ASSASSIN_ROLE = this.getInstanceByClass().getManagerByClass(RoleManager.class).getModuleByClass(Assassin.class);
        this.PREFERENCE = this.getInstanceByClass().getManagerByClass(PreferenceManager.class).getModuleByClass(DisplayRaveArmour.class);
    }

    private void handleCount() {
        this.count++;

        if (this.count >= DyeColor.values().length) {
            this.count = 0;
        }
    }

    @Update(delay = 50L)
    public void onUpdater() {
        this.handleCount();

        if (this.ASSASSIN_ROLE == null || this.PREFERENCE == null) {
            return;
        }

        for (final Player player : this.ASSASSIN_ROLE.getUsers()) {
            if (!(this.isUserByPlayer(player))) {
                continue;
            }

            if (Arrays.stream(player.getEquipment().getArmorContents()).anyMatch(itemStack -> itemStack == null || !(ArmourMaterialType.LEATHER.isValid(itemStack.getType())))) {
                continue;
            }

            if (!(this.PREFERENCE.getUserByPlayer(player).getValue())) {
                continue;
            }

            this.update(player, DyeColor.values()[this.count]);
        }
    }

    private void update(final Player player, final DyeColor dyeColor) {
        final EntityEquipment equipment = player.getEquipment();

        equipment.setHelmet(this.getItemStack(equipment.getHelmet(), dyeColor));
        equipment.setChestplate(this.getItemStack(equipment.getChestplate(), dyeColor));
        equipment.setLeggings(this.getItemStack(equipment.getLeggings(), dyeColor));
        equipment.setBoots(this.getItemStack(equipment.getBoots(), dyeColor));
    }

    private ItemStack getItemStack(final ItemStack itemStack, final DyeColor dyeColor) {
        final LeatherArmorMeta leatherArmorMeta = UtilJava.cast(LeatherArmorMeta.class, itemStack.getItemMeta());

        leatherArmorMeta.setColor(dyeColor.getColor());

        itemStack.setItemMeta(leatherArmorMeta);

        return itemStack;
    }
}