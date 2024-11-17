package me.trae.champions.role.modules;

import me.trae.api.champions.role.RoleChangeEvent;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.core.effect.constants.PotionEffectConstants;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.utility.UtilEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

public class RemovePositivePotionEffectsOnRoleChange extends SpigotListener<Champions, RoleManager> {

    public RemovePositivePotionEffectsOnRoleChange(final RoleManager manager) {
        super(manager);
    }

    @EventHandler
    public void onRoleChange(final RoleChangeEvent event) {
        final Player player = event.getPlayer();

        for (final PotionEffectType potionEffectType : PotionEffectConstants.POSITIVE_POTION_EFFECT_TYPES) {
            UtilEntity.removePotionEffect(player, potionEffectType);
        }
    }
}