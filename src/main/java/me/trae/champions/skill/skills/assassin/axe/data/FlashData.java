package me.trae.champions.skill.skills.assassin.axe.data;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.skills.assassin.axe.data.interfaces.IFlashData;
import org.bukkit.entity.Player;

public class FlashData extends SkillData implements IFlashData {

    private long lastUpdated;
    private int charges;

    public FlashData(final Player player, final int level) {
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
}