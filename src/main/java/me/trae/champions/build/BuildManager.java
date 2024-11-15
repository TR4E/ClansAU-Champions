package me.trae.champions.build;

import me.trae.champions.Champions;
import me.trae.champions.build.commands.BuildCommand;
import me.trae.champions.build.data.RoleBuild;
import me.trae.champions.build.interfaces.IBuildManager;
import me.trae.champions.build.modules.LoadBuildDataOnPlayerJoin;
import me.trae.champions.role.Role;
import me.trae.core.database.repository.containers.RepositoryContainer;
import me.trae.core.framework.SpigotManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BuildManager extends SpigotManager<Champions> implements IBuildManager, RepositoryContainer<BuildRepository> {

    private final Map<UUID, Map<String, Map<Integer, RoleBuild>>> BUILDS = new HashMap<>();

    public BuildManager(final Champions instance) {
        super(instance);
    }

    @Override
    public void registerModules() {
        // Commands
        addModule(new BuildCommand(this));

        // Modules
        addModule(new LoadBuildDataOnPlayerJoin(this));
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
        this.getBuilds().getOrDefault(roleBuild.getUUID(), new HashMap<>()).getOrDefault(roleBuild.getRole(), new HashMap<>()).getOrDefault(roleBuild.getID(), null);
    }

    @Override
    public RoleBuild getBuildByRole(final Player player, final Role role, final int id) {
        return this.getBuilds().getOrDefault(player.getUniqueId(), new HashMap<>()).getOrDefault(role.getName(), new HashMap<>()).getOrDefault(id, null);
    }

    @Override
    public boolean isBuildByRole(final Player player, final Role role, final int id) {
        return this.getBuilds().getOrDefault(player.getUniqueId(), new HashMap<>()).getOrDefault(role.getName(), new HashMap<>()).containsKey(id);
    }
}