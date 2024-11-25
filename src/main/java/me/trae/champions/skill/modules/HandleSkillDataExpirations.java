package me.trae.champions.skill.modules;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.skill.Skill;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.SkillManager;
import me.trae.core.framework.types.frame.SpigotUpdater;
import me.trae.core.updater.annotations.Update;
import me.trae.core.utility.UtilJava;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HandleSkillDataExpirations extends SpigotUpdater<Champions, SkillManager> {

    public HandleSkillDataExpirations(final SkillManager manager) {
        super(manager);
    }

    @Update(delay = 250L)
    public void onUpdater() {
        for (final Role role : this.getInstance().getManagerByClass(RoleManager.class).getModulesByClass(Role.class)) {
            for (final Skill<?, ?> skill : role.getSkillsByClass(Skill.class)) {
                skill.getUsers().values().removeIf(data -> {
                    if (!(data.hasExpired())) {
                        return false;
                    }

                    final Player player = Bukkit.getPlayer(data.getUUID());
                    if (player != null) {
                        skill.reset(player);
                    }

                    skill.onExpire(player, UtilJava.matchlessObjectCast(skill.getClassOfData(), data));
                    return true;
                });
            }
        }
    }
}