package me.trae.champions.skill.enums.interfaces;

import me.trae.core.utility.enums.ActionType;
import org.bukkit.Material;

import java.util.List;

public interface ISkillType {

    String getName();

    ActionType getActionType();

    List<Material> getMaterials();
}