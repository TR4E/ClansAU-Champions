package me.trae.champions.role.menus.interfaces;

import me.trae.api.champions.role.Role;
import org.bukkit.entity.Player;

public interface IRoleSelectionMenu {

    void onClick(final Player player, final Role role);
}