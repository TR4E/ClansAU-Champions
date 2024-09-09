package me.trae.champions.role;

import me.trae.champions.Champions;
import me.trae.champions.role.interfaces.IRole;
import me.trae.core.framework.SpigotModule;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class Role extends SpigotModule<Champions, RoleManager> implements IRole {

    public Role(final RoleManager manager) {
        super(manager);
    }

    @Override
    public String getPrefix() {
        return this.getName().substring(0, 1);
    }

    @Override
    public void reset(final Player player) {
    }

    @Override
    public List<String> getEquipMessage() {
        return Collections.emptyList();
    }
}