package me.trae.champions.skill.skills.knight.passive_a.data.interfaces;

import org.bukkit.entity.LivingEntity;

import java.util.Map;

public interface IThornsData {

    Map<LivingEntity, Long> getHitMap();

    void addHit(final LivingEntity entity);

    Long getHitByEntity(final LivingEntity entity);

    boolean isHitByEntity(final LivingEntity entity);
}