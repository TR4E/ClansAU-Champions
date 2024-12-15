package me.trae.champions.skill.skills.assassin.passive_b.data;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.skills.assassin.passive_b.data.interfaces.IRecallData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RecallData extends SkillData implements IRecallData {

    private long lastUpdated;
    private Location location;
    private double health;

    public RecallData(final Player player, final int level) {
        super(player, level);

        this.update(player);
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
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(final Location location) {
        this.location = location;
    }

    @Override
    public double getHealth() {
        return this.health;
    }

    @Override
    public void setHealth(final double health) {
        this.health = health;
    }

    @Override
    public void update(final Player player) {
        this.updateLastUpdated();
        this.setLocation(player.getLocation());
        this.setHealth(player.getHealth());
    }
}