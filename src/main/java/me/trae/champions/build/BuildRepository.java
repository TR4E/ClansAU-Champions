package me.trae.champions.build;

import me.trae.champions.Champions;
import me.trae.champions.build.data.RoleBuild;
import me.trae.champions.build.enums.BuildProperty;
import me.trae.champions.config.Config;
import me.trae.core.database.query.Query;
import me.trae.core.database.query.types.DeleteQuery;
import me.trae.core.database.query.types.SaveQuery;
import me.trae.core.database.query.types.SingleCallbackQuery;
import me.trae.core.database.query.types.UpdateQuery;
import me.trae.core.database.repository.Repository;
import me.trae.core.database.repository.types.ContainsRepository;
import me.trae.core.database.repository.types.SingleLoadRepository;
import me.trae.core.database.repository.types.UpdateRepository;
import me.trae.core.utility.UtilServer;
import me.trae.core.utility.objects.EnumData;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BuildRepository extends Repository<Champions, BuildManager, Config> implements ContainsRepository<UUID>, UpdateRepository<RoleBuild, BuildProperty>, SingleLoadRepository<UUID> {

    public BuildRepository(final BuildManager manager) {
        super(manager, "Builds");
    }

    @Override
    public Class<Config> getClassOfConfiguration() {
        return Config.class;
    }

    @Override
    public boolean containsData(final UUID uuid) {
        return this.getConfig().getYamlConfiguration().contains(uuid.toString());
    }

    @Override
    public void saveData(final RoleBuild roleBuild) {
        final SaveQuery<BuildProperty> query = new SaveQuery<BuildProperty>() {
            @Override
            public String getKey() {
                return roleBuild.getUUID().toString();
            }

            @Override
            public List<String> getTypes() {
                return Arrays.asList(roleBuild.getRole(), String.valueOf(roleBuild.getID()));
            }

            @Override
            public EnumData<BuildProperty> getData() {
                return roleBuild.getData();
            }
        };

        this.addQuery(query);
    }

    @Override
    public void updateData(final RoleBuild roleBuild, final BuildProperty property) {
        final UpdateQuery<BuildProperty> query = new UpdateQuery<BuildProperty>() {
            @Override
            public String getKey() {
                return roleBuild.getUUID().toString();
            }

            @Override
            public List<String> getTypes() {
                return Arrays.asList(roleBuild.getRole(), String.valueOf(roleBuild.getID()));
            }

            @Override
            public BuildProperty getProperty() {
                return property;
            }

            @Override
            public Object getValue() {
                return roleBuild.getValueByProperty(property);
            }
        };

        this.addQuery(query);
    }

    @Override
    public void deleteData(final RoleBuild roleBuild) {
        final DeleteQuery query = new DeleteQuery() {
            @Override
            public List<String> getTypes() {
                return Arrays.asList(roleBuild.getRole(), String.valueOf(roleBuild.getID()));
            }

            @Override
            public String getKey() {
                return roleBuild.getUUID().toString();
            }
        };

        this.addQuery(query);
    }

    @Override
    public void loadData(final UUID uuid) {
        final SingleCallbackQuery<BuildProperty> query = new SingleCallbackQuery<BuildProperty>() {
            @Override
            public String getKey() {
                return uuid.toString();
            }

            @Override
            public void onCallback(final EnumData<BuildProperty> data) {
                final RoleBuild roleBuild = new RoleBuild(data);

                getManager().addBuild(roleBuild);
            }

            @Override
            public int getTypeIndex() {
                return 2;
            }
        };

        this.addQuery(query);
    }

    @Override
    public boolean isInform(final Query query) {
        return !(query instanceof UpdateQuery<?>);
    }
}