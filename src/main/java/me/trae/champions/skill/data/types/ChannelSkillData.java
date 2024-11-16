package me.trae.champions.skill.data.types;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.data.types.interfaces.IChannelSkillData;
import org.bukkit.entity.Player;

public class ChannelSkillData extends SkillData implements IChannelSkillData {

    private boolean using;

    public ChannelSkillData(final Player player, final int level, final long duration) {
        super(player, level, duration);
    }

    public ChannelSkillData(final Player player, final int level) {
        super(player, level);
    }

    @Override
    public boolean isUsing() {
        return this.using;
    }

    @Override
    public void setUsing(final boolean using) {
        this.using = using;
    }
}