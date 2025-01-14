package me.trae.champions.build.data;

import me.trae.api.champions.role.Role;
import me.trae.champions.Champions;
import me.trae.champions.build.data.interfaces.IRoleBuild;
import me.trae.champions.build.enums.BuildProperty;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.enums.SkillType;
import me.trae.core.database.containers.DataContainer;
import me.trae.core.database.query.constants.DefaultProperty;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.UtilPlugin;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.objects.EnumData;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class RoleBuild implements IRoleBuild, DataContainer<BuildProperty> {

    private final UUID uuid;
    private final String role;
    private final int id;
    private final LinkedHashMap<SkillType, RoleSkill> skills;

    private boolean active;

    public RoleBuild(final UUID uuid, final String role, final int id, final boolean active) {
        this.uuid = uuid;
        this.role = role;
        this.id = id;
        this.skills = new LinkedHashMap<>();
        this.active = active;
    }

    public RoleBuild(final Player player, final Role role, final int id, final boolean active) {
        this(player.getUniqueId(), role.getName(), id, active);
    }

    public RoleBuild(final EnumData<BuildProperty> data) {
        this(UUID.fromString(data.get(String.class, DefaultProperty.KEY)), data.get(String.class, DefaultProperty.getType(0)), data.get(Integer.class, DefaultProperty.getType(1)), data.get(Boolean.class, BuildProperty.ACTIVE));

        data.getList(String.class, BuildProperty.SKILLS).forEach(string -> this.addSkill(new RoleSkill(string.split(":"))));
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public String getRole() {
        return this.role;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public LinkedHashMap<SkillType, RoleSkill> getSkills() {
        return this.skills;
    }

    @Override
    public void addSkill(final RoleSkill roleSkill) {
        this.getSkills().put(roleSkill.getType(), roleSkill);
    }

    @Override
    public void removeSkill(final RoleSkill roleSkill) {
        this.getSkills().remove(roleSkill.getType());
    }

    @Override
    public RoleSkill getRoleSkillByType(final SkillType skillType) {
        return this.getSkills().getOrDefault(skillType, null);
    }

    @Override
    public boolean isRoleSkillByType(final SkillType skillType) {
        return this.getSkills().containsKey(skillType);
    }

    @Override
    public RoleSkill getRoleSkillByName(final String name) {
        for (final RoleSkill roleSkill : this.getSkills().values()) {
            if (!(roleSkill.getName().equals(name))) {
                continue;
            }

            return roleSkill;
        }

        return null;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void setActive(final boolean active) {
        this.active = active;
    }

    @Override
    public String getDisplayName() {
        if (this.getID() == 0) {
            return "Default Build";
        }

        return String.format("Build #%s", this.getID());
    }

    @Override
    public List<String> getEquipMessage() {
        final List<String> list = new ArrayList<>();

        final Role role = UtilJava.cast(Role.class, UtilPlugin.getInstanceByClass(Champions.class).getManagerByClass(RoleManager.class).getModuleByName(this.getRole()));

        for (final SkillType skillType : SkillType.values()) {
            final String typeName = skillType.getName();

            String skillName = "";
            if (this.isRoleSkillByType(skillType)) {
                final RoleSkill roleSkill = this.getRoleSkillByType(skillType);

                if (role.isSubModuleByName(roleSkill.getName())) {
                    if (role.getSubModuleByName(roleSkill.getName()).isEnabled()) {
                        skillName = roleSkill.getDisplayName();
                    } else {
                        skillName = UtilString.format("%s (<red>System Disabled</red>)", roleSkill.getDisplayName());
                    }
                }
            }

            list.add(UtilString.pair(String.format("<green>%s", typeName), String.format("<white>%s", skillName)));
        }

        return list;
    }

    @Override
    public List<BuildProperty> getProperties() {
        return Arrays.asList(BuildProperty.values());
    }

    @Override
    public Object getValueByProperty(final BuildProperty property) {
        switch (property) {
            case ACTIVE:
                return this.isActive();
            case SKILLS:
                return this.getSkills().values().stream().map(RoleSkill::toString).collect(Collectors.toList());
        }

        return null;
    }
}