package me.trae.champions.skill.skills.brute.axe.data;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.skills.brute.axe.data.interfaces.ISeismicSlamData;
import org.bukkit.entity.Player;

public class SeismicSlamData extends SkillData implements ISeismicSlamData {

    private double height;

    public SeismicSlamData(final Player player, final int level) {
        super(player, level);
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public void setHeight(final double height) {
        this.height = height;
    }
}