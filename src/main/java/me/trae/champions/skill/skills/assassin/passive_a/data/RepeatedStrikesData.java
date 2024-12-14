package me.trae.champions.skill.skills.assassin.passive_a.data;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.skills.assassin.passive_a.data.interfaces.IRepeatedStrikesData;
import org.bukkit.entity.Player;

public class RepeatedStrikesData extends SkillData implements IRepeatedStrikesData {

    private int amplifier;

    public RepeatedStrikesData(final Player player, final int level) {
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