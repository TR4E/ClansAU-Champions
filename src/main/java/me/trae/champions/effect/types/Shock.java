package me.trae.champions.effect.types;

import me.trae.champions.Champions;
import me.trae.champions.effect.EffectManager;
import me.trae.core.effect.Effect;
import me.trae.core.effect.data.EffectData;
import org.bukkit.EntityEffect;
import org.bukkit.entity.LivingEntity;

public class Shock extends Effect<Champions, EffectManager> {

    public Shock(final EffectManager manager) {
        super(manager);
    }

    @Override
    public boolean removeOnDeath() {
        return true;
    }

    @Override
    public void onUpdater(final LivingEntity entity, final EffectData data) {
        entity.playEffect(EntityEffect.HURT);
    }
}