package me.trae.champions.build.interfaces;

import me.trae.champions.build.data.RoleBuild;
import me.trae.api.champions.role.Role;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public interface IBuildManager {

    Map<UUID, Map<String, Map<Integer, RoleBuild>>> getBuilds();

    void addBuild(final RoleBuild roleBuild);

    void removeBuild(final RoleBuild roleBuild);

    RoleBuild getBuildByRole(final Player player, final Role role, final int id);

    boolean isBuildByRole(final Player player, final Role role, final int id);
}