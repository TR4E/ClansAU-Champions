package me.trae.champions.skill.skills.ranger.sword.data;

import me.trae.champions.skill.data.types.ChannelSkillData;
import me.trae.champions.skill.skills.ranger.sword.data.interfaces.IWolfsPounceData;
import org.bukkit.entity.Player;

public class WolfsPounceData extends ChannelSkillData implements IWolfsPounceData {

    private long lastUpdated;
    private int charges;
    private boolean pounced;

    public WolfsPounceData(final Player player, final int level) {
        super(player, level);

        this.lastUpdated = System.currentTimeMillis();
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

    @Override
    public boolean isPounced() {
        return this.pounced;
    }

    @Override
    public void setPounced(final boolean pounced) {
        this.pounced = pounced;
    }
}