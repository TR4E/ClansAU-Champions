package me.trae.champions.skill.enums;

import me.trae.champions.skill.enums.interfaces.ISkillType;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.enums.ActionType;
import org.bukkit.Material;

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
}