package me.trae.champions.role.types.submodules;

import me.trae.champions.Champions;
import me.trae.champions.role.Role;
import me.trae.core.framework.types.frame.SpigotSubUpdater;
import me.trae.core.updater.annotations.Update;
import me.trae.core.utility.UtilEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class SpeedEffect extends SpigotSubUpdater<Champions, Role> {

    public SpeedEffect(final Role manager) {
        super(manager);

        this.addPrimitive("Amplifier", 2);
    }

    @Update(delay = 250L)
    public void onUpdater() {
        final int amplifier = this.getPrimitiveCasted(Integer.class, "Amplifier");

        for (final Player player : this.getModule().getUsers()) {
            if (UtilEntity.hasPotionEffect(player, PotionEffectType.SPEED, amplifier)) {
                continue;
            }

            UtilEntity.givePotionEffect(player, PotionEffectType.SPEED, Integer.MAX_VALUE, amplifier);
        }
    }
}