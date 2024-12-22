package me.trae.champions.skill.modules.activation;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.skill.Skill;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.types.PassiveBowSkill;
import me.trae.champions.skill.types.data.BowSkillData;
import me.trae.champions.skill.types.interfaces.IBowSkill;
import me.trae.core.framework.types.frame.SpigotUpdater;
import me.trae.core.updater.annotations.Update;
import me.trae.core.utility.UtilJava;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

public class HandleBowSkillUpdater extends SpigotUpdater<Champions, SkillManager> {

    public HandleBowSkillUpdater(final SkillManager manager) {
        super(manager);
    }

    @Update(delay = 50L)
    public void onUpdater() {
        for (final Role role : this.getInstanceByClass().getManagerByClass(RoleManager.class).getModulesByClass(Role.class)) {
            for (final Skill<?, ? extends BowSkillData> skill : role.getSkillsByClass(Skill.class)) {
                if (!(skill instanceof IBowSkill<?>)) {
                    continue;
                }

                final IBowSkill<?> bowSkill = UtilJava.cast(IBowSkill.class, skill);

                skill.getUsers().values().removeIf(data -> {
                    final Player player = Bukkit.getPlayer(data.getUUID());

                    final Arrow arrow = data.getArrow();
                    if (arrow == null) {
                        return false;
                    }

                    if (!(skill instanceof PassiveBowSkill<?, ?>) || UtilJava.cast(PassiveBowSkill.class, skill).resetDataOnShoot()) {
                        if (arrow.isDead() || !(arrow.isValid())) {
                            return true;
                        }
                    }

                    bowSkill.onUpdater(player, UtilJava.matchlessObjectCast(skill.getClassOfData(), data));
                    return false;
                });
            }
        }
    }
}