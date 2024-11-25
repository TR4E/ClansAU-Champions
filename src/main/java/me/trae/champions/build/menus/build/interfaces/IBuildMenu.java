package me.trae.champions.build.menus.build.interfaces;

import me.trae.api.champions.role.Role;

public interface IBuildMenu {

    Role getUpdatedRole();

    void setUpdatedRole(final Role updatedRole);

    Role getRole();
}