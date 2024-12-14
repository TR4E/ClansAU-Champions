package me.trae.champions.effect.types;

import me.trae.champions.Champions;
import me.trae.champions.effect.EffectManager;
import me.trae.core.effect.Effect;
import me.trae.core.effect.models.PotionEffectContainer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.potion.PotionEffectType;

public class ConcussionEffect extends Effect<Champions, EffectManager> implements PotionEffectContainer, Listener {

    public ConcussionEffect(final EffectManager manager) {
        super(manager);
    }

    @Override
    public boolean removeOnDeath() {
        return true;
    }

    @Override
    public PotionEffectType getPotionEffectType() {
        return PotionEffectType.BLINDNESS;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerToggleSprint(final PlayerToggleSprintEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(this.isUserByEntity(event.getPlayer()))) {
            return;
        }

        event.setCancelled(true);
    }
}