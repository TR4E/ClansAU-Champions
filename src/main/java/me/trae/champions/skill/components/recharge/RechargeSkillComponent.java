package me.trae.champions.skill.components.recharge;

import me.trae.core.utility.UtilTime;

public interface RechargeSkillComponent {

    long getRecharge(final int level);

    default String getRechargeString(final int level) {
        return UtilTime.getTime(this.getRecharge(level));
    }

    default boolean hasRecharge(final int level) {
        return this.getRecharge(level) > 0L;
    }
}