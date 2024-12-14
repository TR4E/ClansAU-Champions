package me.trae.champions.effect.types;

import me.trae.champions.Champions;
import me.trae.champions.effect.EffectManager;
import me.trae.core.effect.Effect;
import me.trae.core.effect.data.EffectData;
import me.trae.core.effect.models.PotionEffectContainer;
import me.trae.core.utility.UtilMessage;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

public class Vulnerable extends Effect<Champions, EffectManager> implements PotionEffectContainer {

    public Vulnerable(final EffectManager manager) {
        super(manager);
    }

    @Override
    public boolean removeOnDeath() {
        return true;
    }

    @Override
    public void onReceive(final LivingEntity entity, final EffectData data) {
        super.onReceive(entity, data);

        UtilMessage.message(entity, "Condition", "You are now Vulnerable!");
    }

    @Override
    public void onRemove(final LivingEntity entity, final EffectData data) {
        super.onRemove(entity, data);

        UtilMessage.message(entity, "Condition", "You are no longer Vulnerable!");
    }

    @Override
    public PotionEffectType getPotionEffectType() {
        return PotionEffectType.WEAKNESS;
    }
}