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
        return Arrays.stream(values()).filter(skillType -> skillType.getMaterials().contains(material)).findFirst().orElse(null);
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
                return Arrays.asList(Material.DIAMOND_SWORD, Material.GOLD_SWORD, Material.IRON_SWORD);
            case AXE:
                return Arrays.asList(Material.DIAMOND_AXE, Material.GOLD_AXE, Material.IRON_AXE);
            case BOW:
                return Collections.singletonList(Material.BOW);
        }

        return Collections.emptyList();
    }

    @Override
    public boolean isItemStack(final ItemStack itemStack) {
        if (!(UtilPlugin.getInstance(Core.class).getManagerByClass(WeaponManager.class).getWeaponByItemStack(itemStack) instanceof ChampionsPvPWeapon)) {
            return false;
        }

        return this.getMaterials().contains(itemStack.getType());
    }
}