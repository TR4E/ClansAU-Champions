package me.trae.champions.skill.skills.ranger.passive_b.data;

import me.trae.champions.skill.skills.ranger.passive_b.data.interfaces.IHuntersThrill;
import me.trae.champions.skill.types.data.BowSkillData;
import org.bukkit.entity.Player;

public class HuntersThrillData extends BowSkillData implements IHuntersThrill {

    private int charges;
    private long lastHit = -1L;

    public HuntersThrillData(final Player player, final int level) {
        super(player, level);
    }

    @Override
    public int getCharges() {
        return this.charges;
    }

    @Override
    public void addCharge() {
        this.charges++;
    }

    @Override
    public long getLastHit() {
        return this.lastHit;
    }

    @Override
    public void updateLastHit() {
        this.lastHit = System.currentTimeMillis();
    }
}