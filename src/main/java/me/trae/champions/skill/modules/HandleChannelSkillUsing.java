package me.trae.champions.skill.modules;

import me.trae.champions.Champions;
import me.trae.champions.role.Role;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.types.ChannelSkill;
import me.trae.core.framework.types.frame.SpigotUpdater;
import me.trae.core.updater.annotations.Update;
import me.trae.core.utility.UtilJava;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HandleChannelSkillUsing extends SpigotUpdater<Champions, SkillManager> {

    public HandleChannelSkillUsing(final SkillManager manager) {
        super(manager);
    }

    @Update
    public void onUpdater() {
        for (final Role role : this.getInstance().getManagerByClass(RoleManager.class).getModulesByClass(Role.class)) {
            for (final ChannelSkill<?, ?> skill : role.getSubModulesByClass(ChannelSkill.class)) {
                skill.getUsers().values().removeIf(data -> {
                    final Player player = Bukkit.getPlayer(data.getUUID());
                    if (player == null) {
                        return true;
                    }

                    if (!(player.isBlocking()) || !(skill.canActivate(player))) {
                        skill.reset(player);
                        return true;
                    }

                    skill.onUsing(player, UtilJava.matchlessObjectCast(skill.getClassOfData(), data));
                    return false;
                });
            }
        }
    }
}