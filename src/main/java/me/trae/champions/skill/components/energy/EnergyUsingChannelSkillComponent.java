package me.trae.champions.skill.components.energy;

import me.trae.core.utility.UtilMath;

public interface EnergyUsingChannelSkillComponent {

    float getEnergyUsing(final int level);

    default String getEnergyUsingString(final int level) {
        return UtilMath.format(this.getEnergyUsing(level), "##.#");
    }

    default boolean hasEnergyUsing(final int level) {
        return this.getEnergyUsing(level) > 0.0F;
    }
}