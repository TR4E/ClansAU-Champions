package me.trae.champions.skill.types;

import me.trae.api.champions.role.Role;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.champions.skill.types.interfaces.IDropSkill;
import me.trae.core.utility.UtilBlock;
import me.trae.core.utility.UtilMessage;
import org.bukkit.entity.Player;

import java.util.Collections;

public abstract class DropSkill<R extends Role, D extends SkillData> extends PassiveSkill<R, D> implements IDropSkill {

    public DropSkill(final R module, final PassiveSkillType passiveSkillType) {
        super(module, passiveSkillType);
    }

    @Override
    public boolean canActivate(final Player player) {
        if (UtilBlock.isInLiquid(player.getLocation())) {
            UtilMessage.simpleMessage(player, "Skill", "You cannot use <green><var></green> while in liquid.", Collections.singletonList(this.getName()));
            return false;
        }

        return true;
    }

    @Override
    public boolean isActive(final Player player) {
        return false;
    }
}