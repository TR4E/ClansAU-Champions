package me.trae.champions.skill.skills.global.modules.abstracts;

import me.trae.champions.Champions;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.skills.global.modules.abstracts.interfaces.IGlobalAbility;
import me.trae.champions.skill.types.GlobalSkill;
import me.trae.core.framework.SpigotModule;
import org.bukkit.entity.Player;

public abstract class GlobalAbility<T extends GlobalSkill<?>> extends SpigotModule<Champions, SkillManager> implements IGlobalAbility<T> {

    public GlobalAbility(final SkillManager manager) {
        super(manager);
    }

    @Override
    public T getSkillByPlayer(final Player player) {
        return this.getManager().getSkillByType(this.getClassOfSkill(), player, SkillType.GLOBAL);
    }

    @Override
    public boolean isSkillByPlayer(final Player player) {
        return this.getSkillByPlayer(player) != null;
    }
}