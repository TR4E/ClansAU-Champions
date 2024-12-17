package me.trae.champions.skill.skills.assassin.axe.data;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.skills.assassin.axe.data.interfaces.IBlinkData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BlinkData extends SkillData implements IBlinkData {

    private long lastUpdated;
    private Location lastLocation;

    public BlinkData(final Player player, final int level, final long duration) {
        super(player, level, duration);
    }

    @Override
    public long getLastUpdated() {
        return this.lastUpdated;
    }

    @Override
    public void updateLastUpdated() {
        this.lastUpdated = System.currentTimeMillis();
    }

    @Override
    public Location getLastLocation() {
        return this.lastLocation;
    }

    @Override
    public void setLastLocation(final Location lastLocation) {
        this.lastLocation = lastLocation;
    }
}