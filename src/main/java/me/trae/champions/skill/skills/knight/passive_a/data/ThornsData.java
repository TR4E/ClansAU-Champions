package me.trae.champions.skill.skills.knight.passive_a.data;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.skills.knight.passive_a.data.interfaces.IThornsData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ThornsData extends SkillData implements IThornsData {

    private final Map<LivingEntity, Long> hitMap;

    public ThornsData(final Player player, final int level) {
        super(player, level);

        this.hitMap = new HashMap<>();
    }

    @Override
    public Map<LivingEntity, Long> getHitMap() {
        return this.hitMap;
    }

    @Override
    public void addHit(final LivingEntity entity) {
        this.getHitMap().put(entity, System.currentTimeMillis());
    }

    @Override
    public Long getHitByEntity(final LivingEntity entity) {
        return this.getHitMap().get(entity);
    }

    @Override
    public boolean isHitByEntity(final LivingEntity entity) {
        return this.getHitMap().containsKey(entity);
    }
}