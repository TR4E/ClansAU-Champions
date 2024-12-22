package me.trae.champions.skill.types;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.skill.Skill;
import me.trae.champions.effect.EffectManager;
import me.trae.champions.effect.types.Silenced;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.champions.skill.types.interfaces.IActiveSkill;
import me.trae.core.utility.UtilBlock;
import me.trae.core.utility.UtilMessage;
import org.bukkit.entity.Player;

import java.util.Collections;

public abstract class ActiveSkill<R extends Role, D extends SkillData> extends Skill<R, D> implements IActiveSkill {

    public ActiveSkill(final R module, final ActiveSkillType activeSkillType) {
        super(module, SkillType.valueOf(activeSkillType.name()));
    }

    @Override
    public boolean canActivate(final Player player) {
        if (UtilBlock.isInLiquid(player.getLocation())) {
            UtilMessage.simpleMessage(player, "Skill", "You cannot use <green><var></green> while in liquid.", Collections.singletonList(this.getName()));
            return false;
        }

        if (this.getInstanceByClass().getManagerByClass(EffectManager.class).getModuleByClass(Silenced.class).isUserByEntity(player)) {
            UtilMessage.simpleMessage(player, "Skill", "You cannot use <green><var></green> while silenced.", Collections.singletonList(this.getName()));
            return false;
        }

        return true;
    }
}