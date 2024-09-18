package me.trae.champions.skill.types;

import me.trae.champions.role.Role;
import me.trae.champions.skill.Skill;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.interfaces.IActiveSkill;
import me.trae.core.utility.UtilBlock;
import me.trae.core.utility.UtilMessage;
import org.bukkit.entity.Player;

import java.util.Collections;

public abstract class ActiveSkill<R extends Role, D extends SkillData> extends Skill<R, D> implements IActiveSkill {

    public ActiveSkill(final R module, final SkillType skillType) {
        super(module, skillType);
    }

    @Override
    public boolean canActivate(final Player player) {
        if (UtilBlock.isInLiquid(player.getLocation())) {
            UtilMessage.simpleMessage(player, "Skill", "You cannot use <green><var></green> while in liquid.", Collections.singletonList(this.getName()));
            return false;
        }

        return true;
    }
}