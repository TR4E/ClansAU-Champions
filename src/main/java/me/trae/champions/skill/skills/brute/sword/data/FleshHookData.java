package me.trae.champions.skill.skills.brute.sword.data;

import me.trae.champions.skill.data.types.ChannelSkillData;
import me.trae.champions.skill.skills.brute.sword.data.interfaces.IFleshHookData;
import org.bukkit.entity.Player;

public class FleshHookData extends ChannelSkillData implements IFleshHookData {

    private final long delay;

    private long lastUpdated;
    private int charges;

    public FleshHookData(final Player player, final int level) {
        super(player, level);

        this.delay = System.currentTimeMillis();
        this.lastUpdated = System.currentTimeMillis();
    }

    @Override
    public long getDelay() {
        return this.delay;
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
    public int getCharges() {
        return this.charges;
    }

    @Override
    public void setCharges(final int charges) {
        this.charges = charges;
    }
}