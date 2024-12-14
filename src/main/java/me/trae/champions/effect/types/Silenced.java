package me.trae.champions.effect.types;

import me.trae.champions.Champions;
import me.trae.champions.effect.EffectManager;
import me.trae.core.effect.Effect;
import me.trae.core.effect.data.EffectData;
import me.trae.core.utility.UtilMessage;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Silenced extends Effect<Champions, EffectManager> {

    public Silenced(final EffectManager manager) {
        super(manager);
    }

    @Override
    public boolean removeOnDeath() {
        return true;
    }

    @Override
    public void onReceive(final LivingEntity entity, final EffectData data) {
        if (entity instanceof Player) {
            UtilMessage.message(entity, "Condition", "You are now Silenced!");
        }
    }

    @Override
    public void onRemove(final LivingEntity entity, final EffectData data) {
        if (entity instanceof Player) {
            UtilMessage.message(entity, "Condition", "You are no longer Silenced!");
        }
    }
}