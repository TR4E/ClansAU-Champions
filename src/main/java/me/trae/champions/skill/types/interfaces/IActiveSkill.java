package me.trae.champions.skill.types.interfaces;

import me.trae.core.utility.enums.ActionType;
import org.bukkit.entity.Player;

public interface IActiveSkill {

    ActionType getActionType();

    void onActivate(final Player player);

    boolean canActivate(final Player player);
}