package me.trae.champions.role.modules;

import me.trae.api.champions.role.RoleChangeEvent;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.core.effect.constants.PotionEffectConstants;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.utility.UtilEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;

public class RemovePotionEffectsOnRoleChange extends SpigotListener<Champions, RoleManager> {

    public RemovePotionEffectsOnRoleChange(final RoleManager manager) {
        super(manager);
    }

    @EventHandler
    public void onRoleChange(final RoleChangeEvent event) {
        final Player player = event.getPlayer();

        for (final PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (PotionEffectConstants.NEGATIVE_POTION_EFFECT_TYPES.contains(potionEffect.getType())) {
                continue;
            }

            UtilEntity.removePotionEffect(player, potionEffect.getType());
        }
    }
}