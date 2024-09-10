package me.trae.champions.role.interfaces;

import me.trae.champions.role.Role;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public interface IRoleManager {

    Map<UUID, Role> getPlayerRoles();

    void setPlayerRole(final Player player, final Role role);

    void removePlayerRole(final Player player);

    Role getPlayerRole(final Player player);

    boolean hasPlayerRole(final Player player);

    Role searchRole(final CommandSender sender, final String name, final boolean inform);

    void giveKit(final Player player, final Role role, final boolean overpowered);
}