package me.trae.champions.skill.types;

import me.trae.api.champions.role.Role;
import me.trae.champions.effect.EffectManager;
import me.trae.champions.effect.types.Silenced;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.champions.skill.types.interfaces.IDropSkill;
import me.trae.core.utility.UtilBlock;
import me.trae.core.utility.UtilMessage;
import org.bukkit.entity.Player;

import java.util.Collections;

public abstract class DropSkill<R extends Role, D extends SkillData> extends PassiveSkill<R, D> implements IDropSkill {

    public DropSkill(final R module) {
        super(module, PassiveSkillType.PASSIVE_B);
    }

    @Override
    public boolean canActivate(final Player player) {
        if (UtilBlock.isInLiquid(player.getLocation())) {
            UtilMessage.simpleMessage(player, "Skill", "You cannot use <green><var></green> while in liquid.", Collections.singletonList(this.getName()));
            return false;
        }

        if (this.getInstance().getManagerByClass(EffectManager.class).getModuleByClass(Silenced.class).isUserByEntity(player)) {
            UtilMessage.simpleMessage(player, "Skill", "You cannot use <green><var></green> while silenced.", Collections.singletonList(this.getName()));
            return false;
        }

        return true;
    }
}