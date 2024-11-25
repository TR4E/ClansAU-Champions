package me.trae.champions.skill.types.interfaces;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.skill.types.data.BowSkillData;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface IBowSkill<D extends BowSkillData> {

    void onFire(final Player player, final D data);

    void onHitByLocation(final Player player, final Location location, final D data);

    void onHitByEntity(final Player damager, final Entity damagee, final CustomPostDamageEvent event, final D data);

    void onUpdater(final Player player, final Arrow arrow);

    default boolean requireFullBowPullRange() {
        return false;
    }
}