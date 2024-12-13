package me.trae.champions.skill.skills.ranger.bow.data.interfaces;

import org.bukkit.entity.Arrow;

import java.util.List;

public interface IPinDownData {

    List<Arrow> getExtraArrows();

    void addExtraArrow(final Arrow arrow);
}