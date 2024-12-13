package me.trae.champions.skill.skills.ranger.passive_a.data;

import me.trae.champions.skill.skills.ranger.passive_a.data.interfaces.ISharpshooterData;
import me.trae.champions.skill.types.data.BowSkillData;
import org.bukkit.entity.Player;

public class SharpshooterData extends BowSkillData implements ISharpshooterData {

    private int charges;
    private long lastHit;

    public SharpshooterData(final Player player, final int level) {
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