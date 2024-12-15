package me.trae.champions.skill.skills.assassin.passive_b.data.interfaces;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IRecallData {

    long getLastUpdated();

    void updateLastUpdated();

    Location getLocation();

    void setLocation(final Location location);

    double getHealth();

    void setHealth(final double heath);

    void update(final Player player);
}