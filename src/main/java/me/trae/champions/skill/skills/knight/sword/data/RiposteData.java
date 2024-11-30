package me.trae.champions.skill.skills.knight.sword.data;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.skills.knight.sword.data.interfaces.IRiposteData;
import org.bukkit.entity.Player;

public class RiposteData extends SkillData implements IRiposteData {

    private boolean invulnerable;

    public RiposteData(final Player player, final int level, final long duration) {
        super(player, level, duration);
    }

    @Override
    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    @Override
    public void setInvulnerable(final boolean invulnerable) {
        this.invulnerable = invulnerable;
    }
}