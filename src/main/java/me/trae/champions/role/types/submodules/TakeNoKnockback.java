package me.trae.champions.role.types.submodules;

import me.trae.api.champions.role.Role;
import me.trae.api.damage.events.damage.CustomPreDamageEvent;
import me.trae.champions.Champions;
import me.trae.core.framework.types.frame.SpigotSubListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class TakeNoKnockback extends SpigotSubListener<Champions, Role> {

    public TakeNoKnockback(final Role module) {
        super(module);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCustomPreDamage(final CustomPreDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamagee() instanceof Player)) {
            return;
        }

        if (!(this.getModule().isUserByPlayer(event.getDamageeByClass(Player.class)))) {
            return;
        }

        event.setKnockback(0.0D);
    }
}