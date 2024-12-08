package me.trae.champions.weapon.weapons.items;

import me.trae.champions.Champions;
import me.trae.champions.weapon.WeaponManager;
import me.trae.core.Core;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.throwable.Throwable;
import me.trae.core.throwable.ThrowableManager;
import me.trae.core.throwable.events.ThrowableCollideEntityEvent;
import me.trae.core.throwable.events.ThrowableGroundedEvent;
import me.trae.core.utility.*;
import me.trae.core.utility.enums.ActionType;
import me.trae.core.utility.objects.SoundCreator;
import me.trae.core.weapon.data.WeaponData;
import me.trae.core.weapon.events.WeaponFriendlyFireEvent;
import me.trae.core.weapon.events.WeaponLocationEvent;
import me.trae.core.weapon.types.ActiveCustomItem;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class ExtinguishingPotion extends ActiveCustomItem<Champions, WeaponManager, WeaponData> implements Listener {

    @ConfigInject(type = Long.class, path = "Left-Click-Recharge", defaultValue = "5_000")
    private long leftClickRecharge;

    @ConfigInject(type = Long.class, path = "Right-Click-Recharge", defaultValue = "5_000")
    private long rightClickRecharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "30_000")
    private long duration;

    @ConfigInject(type = Integer.class, path = "Amplifier", defaultValue = "1")
    private int amplifier;

    @ConfigInject(type = Integer.class, path = "Ground-Block-Distance", defaultValue = "1")
    private int groundBlockDistance;

    @ConfigInject(type = Double.class, path = "Item-Velocity", defaultValue = "1.8")
    private double itemVelocity;

    @ConfigInject(type = Boolean.class, path = "Friendly-Fire", defaultValue = "true")
    private boolean friendlyFire;

    public ExtinguishingPotion(final WeaponManager manager) {
        super(manager, new ItemStack(Material.POTION, 1, (short) 0), ActionType.ALL);
    }

    @Override
    public int getModel() {
        return 851346;
    }

    @Override
    public Class<WeaponData> getClassOfData() {
        return WeaponData.class;
    }

    @Override
    public boolean isNaturallyObtained() {
        return true;
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                UtilString.pair("<gray>Left-Click", "<yellow>Throw"),
                "   Douses Players",
                "   Douses Fires",
                "",
                UtilString.pair("<gray>Right-Click", "<yellow>Drink"),
                "   Douses Self",
                String.format("   Fire Resistance <green>%s</green> for <green>%s</green>", this.amplifier, UtilTime.getTime(this.duration))
        };
    }

    @Override
    public String getAbilityName(final ActionType actionType) {
        switch (actionType) {
            case LEFT_CLICK:
                return "Extinguish Throw";
            case RIGHT_CLICK:
                return "Extinguish Drink";
        }

        return this.getAbilityName();
    }

    @Override
    public void onActivate(final Player player, final ActionType actionType) {
        switch (actionType) {
            case LEFT_CLICK: {
                this.onLeftClick(player);
                break;
            }
            case RIGHT_CLICK: {
                this.onRightClick(player);
                break;
            }
        }
    }

    private void onLeftClick(final Player player) {
        final Throwable throwable = new Throwable(this.getAbilityName(ActionType.LEFT_CLICK), this.getItemStack(), player, this.duration, player.getLocation().getDirection().multiply(this.itemVelocity));

        this.getInstance(Core.class).getManagerByClass(ThrowableManager.class).addThrowable(throwable);

        UtilMessage.simpleMessage(player, this.getName(), "You used <var>.", Collections.singletonList(this.getDisplayAbilityName(ActionType.LEFT_CLICK)));
    }

    private void onRightClick(final Player player) {
        player.setFireTicks(0);

        UtilEntity.givePotionEffect(player, PotionEffectType.FIRE_RESISTANCE, ExtinguishingPotion.this.amplifier, ExtinguishingPotion.this.duration);

        UtilBlock.splash(player.getEyeLocation());

        new SoundCreator(Sound.DRINK).play(player.getLocation());

        UtilMessage.simpleMessage(player, this.getName(), "You used <var>.", Collections.singletonList(this.getDisplayAbilityName(ActionType.RIGHT_CLICK)));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onThrowableGrounded(final ThrowableGroundedEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.isThrowableName(this.getAbilityName(ActionType.LEFT_CLICK)))) {
            return;
        }

        event.setCancelled(true);

        final Location location = event.getLocation();

        if (UtilServer.getEvent(new WeaponLocationEvent(this, location)).isCancelled()) {
            return;
        }

        for (final Block block : UtilBlock.getInRadius(location, this.groundBlockDistance)) {
            if (block.getType() == Material.AIR || UtilBlock.isInLiquid(block.getLocation())) {
                continue;
            }

            if (UtilServer.getEvent(new WeaponLocationEvent(this, block.getLocation())).isCancelled()) {
                continue;
            }

            if (block.getType() == Material.FIRE) {
                block.setType(Material.AIR);
                block.getWorld().playEffect(block.getLocation(), Effect.EXTINGUISH, 1);
            }

            UtilBlock.splash(block.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onThrowableCollideEntity(final ThrowableCollideEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.isThrowableName(this.getAbilityName(ActionType.LEFT_CLICK)))) {
            return;
        }

        final LivingEntity target = event.getTarget();

        if (UtilServer.getEvent(new WeaponLocationEvent(this, target.getLocation())).isCancelled()) {
            return;
        }

        if (event.getThrowable().isCollided(target)) {
            return;
        }

        final Player throwerPlayer = event.getThrowable().getThrowerPlayer();

        if (target instanceof Player) {
            final WeaponFriendlyFireEvent weaponFriendlyFireEvent = new WeaponFriendlyFireEvent(this, throwerPlayer, event.getTargetByClass(Player.class));
            UtilServer.callEvent(weaponFriendlyFireEvent);
            if (weaponFriendlyFireEvent.isCancelled()) {
                return;
            }

            if (!(this.friendlyFire)) {
                if (target == throwerPlayer) {
                    return;
                }

                if (!(weaponFriendlyFireEvent.isVulnerable())) {
                    return;
                }
            }
        }

        target.setFireTicks(0);

        UtilEntity.givePotionEffect(target, PotionEffectType.FIRE_RESISTANCE, ExtinguishingPotion.this.amplifier, ExtinguishingPotion.this.duration);

        UtilBlock.splash(target.getEyeLocation());
    }

    @Override
    public float getEnergy(final ActionType actionType) {
        return 0.0F;
    }

    @Override
    public long getRecharge(final ActionType actionType) {
        switch (actionType) {
            case LEFT_CLICK:
                return this.leftClickRecharge;
            case RIGHT_CLICK:
                return this.rightClickRecharge;
        }

        return 0L;
    }
}