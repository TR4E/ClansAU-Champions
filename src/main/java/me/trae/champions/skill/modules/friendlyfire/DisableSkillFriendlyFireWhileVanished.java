package me.trae.champions.skill.modules.friendlyfire;

import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.champions.Champions;
import me.trae.champions.skill.SkillManager;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.utility.injectors.annotations.Inject;
import me.trae.core.vanish.VanishManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class DisableSkillFriendlyFireWhileVanished extends SpigotListener<Champions, SkillManager> {

    @Inject
    private VanishManager vanishManager;

    public DisableSkillFriendlyFireWhileVanished(final SkillManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSkillFriendlyFire(final SkillFriendlyFireEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(this.vanishManager.isVanishedByPlayer(event.getTarget()))) {
            return;
        }

        event.setCancelled(true);
    }
}