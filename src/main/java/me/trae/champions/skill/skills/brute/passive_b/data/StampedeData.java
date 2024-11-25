package me.trae.champions.skill.skills.brute.passive_b.data;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.skills.brute.passive_b.data.interfaces.IStampedeData;
import org.bukkit.entity.Player;

public class StampedeData extends SkillData implements IStampedeData {

    private int amplifier;
    private long lastUpdated;

    public StampedeData(final Player player, final int level) {
        super(player, level);

        this.amplifier = 0;
        this.lastUpdated = System.currentTimeMillis();
    }

    @Override
    public int getAmplifier() {
        return this.amplifier;
    }

    @Override
    public void setAmplifier(final int amplifier) {
        this.amplifier = amplifier;
    }

    @Override
    public boolean hasAmplifier() {
        return this.getAmplifier() > 0;
    }

    @Override
    public long getLastUpdated() {
        return this.lastUpdated;
    }

    @Override
    public void updateLastUpdated() {
        this.lastUpdated = System.currentTimeMillis();
    }
}