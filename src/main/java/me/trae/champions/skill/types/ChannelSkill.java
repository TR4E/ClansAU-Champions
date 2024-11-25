package me.trae.champions.skill.types;

import me.trae.api.champions.role.Role;
import me.trae.champions.skill.data.types.ChannelSkillData;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.champions.skill.types.interfaces.IChannelSkill;
import org.bukkit.entity.Player;

public abstract class ChannelSkill<R extends Role, D extends ChannelSkillData> extends ActiveSkill<R, D> implements IChannelSkill<D> {

    public ChannelSkill(final R module) {
        super(module, ActiveSkillType.SWORD);
    }

    @Override
    public boolean isUsing(final Player player) {
        return this.isUserByPlayer(player) && this.getUserByPlayer(player).isUsing();
    }
}