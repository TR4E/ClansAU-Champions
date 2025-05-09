package me.trae.champions.skill.enums;

import me.trae.champions.skill.enums.interfaces.ISkillType;
import me.trae.champions.weapon.types.ChampionsPvPWeapon;
import me.trae.core.Core;
import me.trae.core.utility.UtilPlugin;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.enums.ActionType;
import me.trae.core.weapon.WeaponManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum SkillType implements ISkillType {

    SWORD, AXE, BOW, PASSIVE_A, PASSIVE_B, GLOBAL;

    private final String name;

    SkillType() {
        this.name = UtilString.clean(this.name());
    }

    public static SkillType getByMaterial(final Material material) {
        for (final SkillType skillType : values()) {
            if (!(skillType.getMaterials().contains(material))) {
                continue;
            }

            return skillType;
        }

        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ActionType getActionType() {
        switch (this) {
            case SWORD:
            case AXE:
                return ActionType.RIGHT_CLICK;
            case BOW:
                return ActionType.LEFT_CLICK;
        }

        return ActionType.NONE;
    }

    @Override
    public List<Material> getMaterials() {
        switch (this) {
            case SWORD:
                return Arrays.asList(Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD);
            case AXE:
                return Arrays.asList(Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE);
            case BOW:
                return Collections.singletonList(Material.BOW);
        }

        return Collections.emptyList();
    }

    @Override
    public boolean isItemStack(final ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        if (!(UtilPlugin.getInstanceByClass(Core.class).getManagerByClass(WeaponManager.class).getWeaponByItemStack(itemStack) instanceof ChampionsPvPWeapon)) {
            return false;
        }

        return this.getMaterials().contains(itemStack.getType());
    }

    @Override
    public ItemStack getDisplayItemStack() {
        Material material = Material.INK_SACK;
        short durability = 0;

        switch (this) {
            case SWORD:
            case AXE:
            case BOW:
                material = this.getMaterials().get(0);
                break;
            case PASSIVE_A:
                durability = 1;
                break;
            case PASSIVE_B:
                durability = 14;
                break;
            case GLOBAL:
                durability = 11;
                break;
        }

        return new ItemStack(material, 1, durability);
    }

    @Override
    public boolean isActive() {
        return Arrays.asList(SWORD, AXE, BOW).contains(this);
    }

    @Override
    public boolean isPassive() {
        return Arrays.asList(PASSIVE_A, PASSIVE_B).contains(this);
    }

    @Override
    public boolean isGlobal() {
        return this == GLOBAL;
    }
}