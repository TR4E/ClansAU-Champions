package me.trae.champions.skill.skills.brute.passive_b.data;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.skills.brute.passive_b.data.interfaces.IBloodlustData;
import org.bukkit.entity.Player;

public class BloodlustData extends SkillData implements IBloodlustData {

    private int amplifier;

    public BloodlustData(final Player player, final int level) {
        super(player, level);
    }

    @Override
    public int getAmplifier() {
        return this.amplifier;
    }

    @Override
    public void setAmplifier(final int amplifier) {
        this.amplifier = amplifier;
    }
}