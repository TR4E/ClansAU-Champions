package me.trae.champions.skill.skills.assassin.axe.data.interfaces;

import org.bukkit.Location;

public interface IBlinkData {

    long getLastUpdated();

    void updateLastUpdated();

    Location getLastLocation();

    void setLastLocation(final Location lastLocation);
}