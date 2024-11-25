package me.trae.champions.skill.types.data;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.data.interfaces.IBowSkillData;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

public class BowSkillData extends SkillData implements IBowSkillData {

    private Location location;
    private Arrow arrow;

    public BowSkillData(final Player player, final int level, final long duration) {
        super(player, level, duration);
    }

    public BowSkillData(final Player player, final int level) {
        super(player, level);
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public Arrow getArrow() {
        return this.arrow;
    }

    @Override
    public void setArrow(final Arrow arrow) {
        this.location = arrow.getLocation();
        this.arrow = arrow;
    }

    @Override
    public boolean isArrow(final Arrow arrow) {
        return this.getArrow().equals(arrow);
    }

    @Override
    public boolean hasArrow() {
        return this.getArrow() != null;
    }
}