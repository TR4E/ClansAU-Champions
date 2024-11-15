package me.trae.champions.skill.components;

import me.trae.core.utility.UtilMath;

public interface EnergySkillComponent {

    float getEnergy(final int level);

    default String getEnergyString(final int level) {
        return UtilMath.format(this.getEnergy(level), "##.#");
    }

    default boolean hasEnergy(final int level) {
        return this.getEnergy(level) > 0.0F;
    }
}