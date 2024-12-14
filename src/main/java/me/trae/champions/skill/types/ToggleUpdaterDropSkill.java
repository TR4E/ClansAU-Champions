package me.trae.champions.skill.types;

import me.trae.api.champions.role.Role;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.data.types.ToggleUpdaterDropSkillData;
import me.trae.champions.skill.types.interfaces.IToggleUpdaterDropSkill;
import me.trae.champions.skill.types.models.ToggleSkill;
import me.trae.core.utility.UtilJava;
import org.bukkit.entity.Player;

public abstract class ToggleUpdaterDropSkill<R extends Role, D extends ToggleUpdaterDropSkillData> extends DropSkill<R, D> implements IToggleUpdaterDropSkill<D> {

    public ToggleUpdaterDropSkill(final R module) {
        super(module);
    }

    @Override
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            this.getUserByPlayer(player).setUsing(false);
        }
    }

    @Override
    public boolean isUsingByPlayer(final Player player) {
        return this.isUserByPlayer(player) && this.getUserByPlayer(player).isUsing();
    }
}