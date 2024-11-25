package me.trae.champions.skill.skills.knight.passive_b.data;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.skills.knight.passive_b.data.interfaces.ISwordsmanshipData;
import org.bukkit.entity.Player;

public class SwordsmanshipData extends SkillData implements ISwordsmanshipData {

    private int charges;
    private long lastUpdated;

    public SwordsmanshipData(final Player player, final int level) {
        super(player, level);

        this.charges = 0;
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
    public boolean hasCharges() {
        return this.getCharges() > 0;
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