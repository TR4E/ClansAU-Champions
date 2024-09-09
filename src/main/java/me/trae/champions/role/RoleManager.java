package me.trae.champions.role;

import me.trae.champions.Champions;
import me.trae.champions.role.interfaces.IRoleManager;
import me.trae.champions.role.modules.HandleRoleEquip;
import me.trae.champions.role.types.*;
import me.trae.core.framework.SpigotManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RoleManager extends SpigotManager<Champions> implements IRoleManager {

    private final Map<UUID, Role> PLAYER_ROLES = new HashMap<>();

    public RoleManager(final Champions instance) {
        super(instance);
    }

    @Override
    public void registerModules() {
        // Roles
        addModule(new Assassin(this));
        addModule(new Brute(this));
        addModule(new Knight(this));
        addModule(new Mage(this));
        addModule(new Ranger(this));

        // Modules
        addModule(new HandleRoleEquip(this));
    }

    @Override
    public Map<UUID, Role> getPlayerRoles() {
        return this.PLAYER_ROLES;
    }

    @Override
    public void setPlayerRole(final Player player, final Role role) {
        this.getPlayerRoles().put(player.getUniqueId(), role);
    }

    @Override
    public void removePlayerRole(final Player player) {
        this.getPlayerRoles().remove(player.getUniqueId());
    }

    @Override
    public Role getPlayerRole(final Player player) {
        return this.getPlayerRoles().getOrDefault(player.getUniqueId(), null);
    }

    @Override
    public boolean hasPlayerRole(final Player player) {
        return this.getPlayerRoles().containsKey(player.getUniqueId());
    }
}