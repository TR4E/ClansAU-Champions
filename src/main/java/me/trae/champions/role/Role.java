package me.trae.champions.role;

import me.trae.champions.Champions;
import me.trae.champions.role.interfaces.IRole;
import me.trae.champions.skill.Skill;
import me.trae.champions.skill.enums.SkillType;
import me.trae.core.framework.SpigotModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Role extends SpigotModule<Champions, RoleManager> implements IRole {

    public Role(final RoleManager manager) {
        super(manager);
    }

    @Override
    public <E extends Skill<?, ?>> List<E> getSkillsByClass(final Class<E> clazz) {
        return this.getSubModulesByClass(clazz);
    }

    @Override
    public <E extends Skill<?, ?>> E getSkillByType(final Class<E> clazz, final SkillType skillType) {
        for (final E skill : this.getSubModulesByClass(clazz)) {
            if (skill.getType() != skillType) {
                continue;
            }

            return skill;
        }

        return null;
    }

    @Override
    public List<Player> getUsers() {
        final List<Player> list = new ArrayList<>();

        for (final Map.Entry<UUID, Role> entry : this.getManager().getPlayerRoles().entrySet()) {
            if (entry.getValue() != this) {
                continue;
            }

            final Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) {
                continue;
            }

            list.add(player);
        }

        return list;
    }

    @Override
    public boolean isUserByPlayer(final Player player) {
        return this.getManager().getPlayerRole(player) == this;
    }

    @Override
    public String getPrefix() {
        return this.getName().substring(0, 1);
    }

    @Override
    public void reset(final Player player) {
        for (final Skill<?, ?> skill : this.getSkillsByClass(Skill.class)) {
            skill.reset(player);

            if (skill.isUserByPlayer(player)) {
                skill.removeUser(player);
            }
        }
    }

    @Override
    public List<String> getEquipMessage() {
        final List<String> list = new ArrayList<>();

        for (final SkillType skillType : SkillType.values()) {
            String skillName = "";

            final Skill<?, ?> skill = this.getSkillByType(Skill.class, skillType);
            if (skill != null) {
                skillName = skill.getName();
            }

            list.add(String.format("<green>%s: <white>%s", skillType.getName(), skillName));
        }

        return list;
    }
}