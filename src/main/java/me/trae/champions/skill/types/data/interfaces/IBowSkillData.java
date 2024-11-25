package me.trae.champions.skill.types.data.interfaces;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;

public interface IBowSkillData {

    Location getLocation();

    Arrow getArrow();

    void setArrow(final Arrow arrow);

    boolean isArrow(final Arrow arrow);

    boolean hasArrow();
}