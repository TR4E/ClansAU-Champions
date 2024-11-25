package me.trae.champions.role.types.submodules;

import me.trae.api.champions.role.Role;
import me.trae.champions.Champions;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.framework.types.frame.SpigotSubUpdater;
import me.trae.core.updater.annotations.Update;
import me.trae.core.utility.UtilEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class SpeedEffect extends SpigotSubUpdater<Champions, Role> {

    @ConfigInject(type = Integer.class, path = "Amplifier", defaultValue = "2")
    private int amplifier;

    public SpeedEffect(final Role manager) {
        super(manager);
    }

    @Update(delay = 250L)
    public void onUpdater() {
        for (final Player player : this.getModule().getUsers()) {
            if (UtilEntity.hasPotionEffect(player, PotionEffectType.SPEED, this.amplifier)) {
                continue;
            }

            UtilEntity.givePotionEffect(player, PotionEffectType.SPEED, this.amplifier, Integer.MAX_VALUE);
        }
    }
}