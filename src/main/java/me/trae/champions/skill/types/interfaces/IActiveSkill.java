package me.trae.champions.skill.types.interfaces;

import org.bukkit.entity.Player;

public interface IActiveSkill {

    void onActivate(final Player player);

    boolean canActivate(final Player player);
}