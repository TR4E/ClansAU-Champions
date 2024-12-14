package me.trae.champions.skill.data.types;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.data.types.interfaces.IToggleUpdaterDropSkillData;
import org.bukkit.entity.Player;

public class ToggleUpdaterDropSkillData extends SkillData implements IToggleUpdaterDropSkillData {

    private boolean using;

    public ToggleUpdaterDropSkillData(final Player player, final int level) {
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