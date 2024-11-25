package me.trae.champions.skill.types.interfaces;

import me.trae.champions.skill.types.data.BowSkillData;
import org.bukkit.entity.Player;

public interface IActiveBowSkill<D extends BowSkillData> extends IBowSkill<D> {

    void onPrepare(final Player player, final int level);
}