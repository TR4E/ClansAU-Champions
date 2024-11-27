package me.trae.champions.build.interfaces;

import me.trae.api.champions.role.Role;
import me.trae.champions.build.data.RoleBuild;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IBuildManager {

    Map<UUID, Map<String, Map<Integer, RoleBuild>>> getBuilds();

    void addBuild(final RoleBuild roleBuild);

    void removeBuild(final RoleBuild roleBuild);

    void removeBuilds(final Player player);

    RoleBuild getBuildByID(final Player player, final Role role, final int id);

    Map<Integer, RoleBuild> getBuildsByRole(final Player player, final Role role);

    List<RoleBuild> getBuildsByPlayer(final Player player);

    boolean isBuildByID(final Player player, final Role role, final int id);

    boolean hasBuild(final Player player);

    void setActiveRoleBuild(final Player player, final Role role, final RoleBuild roleBuild);

    int getSkillPoints(final Role role, final RoleBuild roleBuild);

    void openMenu(final Player player);

    void fixRoleBuild(final Player player);

    boolean isRoleBuildNeedFix(final Player player);
}