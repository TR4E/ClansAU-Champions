package me.trae.champions.skill.types.interfaces;

import me.trae.champions.skill.types.data.BowSkillData;
import org.bukkit.entity.Player;

public interface IPassiveBowSkill<D extends BowSkillData> extends IBowSkill<D> {

    void onActivate(final Player player, final int level);

    default boolean resetDataOnShoot() {
        return true;
    }
}