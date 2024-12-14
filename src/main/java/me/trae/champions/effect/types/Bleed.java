package me.trae.champions.effect.types;

import me.trae.api.damage.utility.UtilDamage;
import me.trae.champions.Champions;
import me.trae.champions.effect.EffectManager;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.effect.Effect;
import me.trae.core.effect.data.EffectData;
import me.trae.core.utility.UtilParticle;
import me.trae.core.utility.UtilTime;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Map;

public class Bleed extends Effect<Champions, EffectManager> {

    private final Map<LivingEntity, Long> MAP = new HashMap<>();

    @ConfigInject(type = Double.class, path = "Damage", defaultValue = "2.0")
    private double damage;

    @ConfigInject(type = Long.class, path = "Delay", defaultValue = "1_000")
    private long delay;

    public Bleed(final EffectManager manager) {
        super(manager);
    }

    @Override
    public boolean removeOnDeath() {
        return true;
    }

    @Override
    public void onUpdater(final LivingEntity entity, final EffectData data) {
        if (this.MAP.containsKey(entity)) {
            if (!(UtilTime.elapsed(this.MAP.get(entity), this.delay))) {
                return;
            }

            UtilDamage.damage(entity, data.getCause(), EntityDamageEvent.DamageCause.CUSTOM, this.damage, this.delay);
            UtilParticle.playBloodEffect(entity.getLocation());
        }

        this.MAP.put(entity, System.currentTimeMillis());
    }

    @Override
    public void onRemove(final LivingEntity entity, final EffectData data) {
        this.MAP.remove(entity);
    }
}