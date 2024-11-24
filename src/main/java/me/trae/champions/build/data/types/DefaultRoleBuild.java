package me.trae.champions.build.data.types;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.skill.Skill;
import me.trae.champions.build.data.RoleBuild;
import me.trae.champions.build.data.RoleSkill;
import me.trae.champions.skill.enums.SkillType;
import org.bukkit.entity.Player;

public class DefaultRoleBuild extends RoleBuild {

    public DefaultRoleBuild(final Player player, final Role role) {
        super(player, role, 0, false);

        for (final SkillType skillType : SkillType.values()) {
            for (final Skill<?, ?> skill : role.getSkillsByType(Skill.class, skillType)) {
                if (skill.getDefaultLevel() <= 0) {
                    continue;
                }

                this.addSkill(new RoleSkill(skill, skill.getDefaultLevel()));
            }
        }
    }
}