package me.trae.champions.build;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.skill.Skill;
import me.trae.champions.Champions;
import me.trae.champions.build.commands.BuildCommand;
import me.trae.champions.build.data.RoleBuild;
import me.trae.champions.build.data.RoleSkill;
import me.trae.champions.build.enums.BuildProperty;
import me.trae.champions.build.interfaces.IBuildManager;
import me.trae.champions.build.menus.build.BuildCustomizationMenu;
import me.trae.champions.build.modules.HandleClassCustomizationTable;
import me.trae.champions.build.modules.data.HandleLoadBuildDataOnPlayerJoin;
import me.trae.champions.build.modules.data.HandleRemoveBuildDataOnPlayerQuit;
import me.trae.champions.build.modules.menu.HandleDisplayEquipMessageOnBuildMenuUpdate;
import me.trae.champions.role.RoleManager;
import me.trae.champions.role.menus.RoleSelectionMenu;
import me.trae.core.database.repository.containers.RepositoryContainer;
import me.trae.core.framework.SpigotManager;
import me.trae.core.utility.UtilMenu;
import org.bukkit.entity.Player;

import java.util.*;

public class BuildManager extends SpigotManager<Champions> implements IBuildManager, RepositoryContainer<BuildRepository> {

    private final Map<UUID, Map<String, Map<Integer, RoleBuild>>> BUILDS = new HashMap<>();

    public BuildManager(final Champions instance) {
        super(instance);
    }

    @Override
    public void registerModules() {
        // Commands
        addModule(new BuildCommand(this));

        // Data Modules
        addModule(new HandleLoadBuildDataOnPlayerJoin(this));
        addModule(new HandleRemoveBuildDataOnPlayerQuit(this));

        // Menu Modules
        addModule(new HandleDisplayEquipMessageOnBuildMenuUpdate(this));

        // Modules
        addModule(new HandleClassCustomizationTable(this));
    }

    @Override
    public Class<BuildRepository> getClassOfRepository() {
        return BuildRepository.class;
    }

    @Override
    public Map<UUID, Map<String, Map<Integer, RoleBuild>>> getBuilds() {
        return this.BUILDS;
    }

    @Override
    public void addBuild(final RoleBuild roleBuild) {
        if (!(this.getBuilds().containsKey(roleBuild.getUUID()))) {
            this.getBuilds().put(roleBuild.getUUID(), new HashMap<>());
        }

        final Map<String, Map<Integer, RoleBuild>> map = this.getBuilds().get(roleBuild.getUUID());

        if (!(map.containsKey(roleBuild.getRole()))) {
            map.put(roleBuild.getRole(), new HashMap<>());
        }

        final Map<Integer, RoleBuild> map2 = map.get(roleBuild.getRole());

        map2.put(roleBuild.getID(), roleBuild);
    }

    @Override
    public void removeBuild(final RoleBuild roleBuild) {
        if (this.getBuilds().containsKey(roleBuild.getUUID())) {
            final Map<String, Map<Integer, RoleBuild>> map = this.getBuilds().get(roleBuild.getUUID());

            if (map.containsKey(roleBuild.getRole())) {
                final Map<Integer, RoleBuild> map2 = map.get(roleBuild.getRole());

                map2.remove(roleBuild.getID());

                if (map2.isEmpty()) {
                    map.remove(roleBuild.getRole());
                }
            }

            if (map.isEmpty()) {
                this.getBuilds().remove(roleBuild.getUUID());
            }
        }
    }

    @Override
    public void removeBuilds(final Player player) {
        this.getBuilds().remove(player.getUniqueId());
    }

    @Override
    public RoleBuild getBuildByID(final Player player, final Role role, final int id) {
        return this.getBuilds().getOrDefault(player.getUniqueId(), new HashMap<>()).getOrDefault(role.getName(), new HashMap<>()).getOrDefault(id, null);
    }

    @Override
    public Map<Integer, RoleBuild> getBuildsByRole(final Player player, final Role role) {
        return this.getBuilds().getOrDefault(player.getUniqueId(), new HashMap<>()).getOrDefault(role.getName(), new HashMap<>());
    }

    @Override
    public List<RoleBuild> getBuildsByPlayer(final Player player) {
        final List<RoleBuild> list = new ArrayList<>();

        for (final Map<Integer, RoleBuild> map : this.getBuilds().getOrDefault(player.getUniqueId(), new HashMap<>()).values()) {
            list.addAll(map.values());
        }

        return list;
    }

    @Override
    public boolean isBuildByID(final Player player, final Role role, final int id) {
        return this.getBuilds().getOrDefault(player.getUniqueId(), new HashMap<>()).getOrDefault(role.getName(), new HashMap<>()).containsKey(id);
    }

    @Override
    public boolean hasBuild(final Player player) {
        return this.getBuilds().containsKey(player.getUniqueId());
    }

    @Override
    public void setActiveRoleBuild(final Player player, final Role role, final RoleBuild roleBuild) {
        for (final RoleBuild oldRoleBuild : this.getBuildsByRole(player, role).values()) {
            if (!(oldRoleBuild.isActive())) {
                continue;
            }

            if (roleBuild != null && oldRoleBuild == roleBuild) {
                continue;
            }

            oldRoleBuild.setActive(false);

            if (oldRoleBuild.getID() != 0) {
                this.getRepository().updateData(oldRoleBuild, BuildProperty.ACTIVE);
            }
        }

        if (roleBuild == null || roleBuild.isActive()) {
            return;
        }

        roleBuild.setActive(true);

        if (roleBuild.getID() != 0) {
            this.getRepository().updateData(roleBuild, BuildProperty.ACTIVE);
        }
    }

    @Override
    public int getSkillPoints(final Role role, final RoleBuild roleBuild) {
        int points = role.getMaxSkillTokens();

        for (final RoleSkill roleSkill : roleBuild.getSkills().values()) {
            final Skill<?, ?> skill = role.getSkillByType(Skill.class, roleSkill.getType());
            if (skill == null) {
                continue;
            }

            points -= roleSkill.getLevel() * skill.getTokenCost();
        }

        return points;
    }

    @Override
    public void openMenu(final Player player) {
        UtilMenu.open(new RoleSelectionMenu(this.getInstance().getManagerByClass(RoleManager.class), player) {
            @Override
            public void onClick(final Player player, final Role role) {
                UtilMenu.open(new BuildCustomizationMenu(BuildManager.this, player, role) {
                    @Override
                    public Role getRole() {
                        return role;
                    }
                });
            }
        });
    }

    @Override
    public boolean isLocked() {
        return true;
    }
}