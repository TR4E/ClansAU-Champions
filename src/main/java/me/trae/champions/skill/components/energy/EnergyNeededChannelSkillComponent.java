package me.trae.champions.skill.components.energy;

import me.trae.core.utility.UtilMath;

public interface EnergyNeededChannelSkillComponent {

    float getEnergyNeeded(final int level);

    default String getEnergyNeededString(final int level) {
        return UtilMath.format(this.getEnergyNeeded(level), "##.#");
    }

    default boolean hasEnergyNeeded(final int level) {
        return this.getEnergyNeeded(level) > 0.0F;
    }
}