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
import me.trae.core.weapon.data.WeaponData;
import me.trae.core.weapon.events.WeaponFriendlyFireEvent;
import me.trae.core.weapon.events.WeaponLocationEvent;
import me.trae.core.weapon.types.ActiveCustomItem;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Collections;

public class GravityGrenade extends ActiveCustomItem<Champions, WeaponManager, WeaponData> implements Listener {

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "15_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "5_000")
    private long duration;

    @ConfigInject(type = Double.class, path = "Item-Velocity", defaultValue = "1.3")
    private double itemVelocity;

    @ConfigInject(type = Integer.class, path = "Particle-Radius", defaultValue = "5")
    private int particleRadius;

    @ConfigInject(type = Integer.class, path = "Particle-Count", defaultValue = "50")
    private int particleCount;

    @ConfigInject(type = Long.class, path = "Blindness-Duration", defaultValue = "2_000")
    private long blindnessDuration;

    @ConfigInject(type = Integer.class, path = "Blindness-Amplifier", defaultValue = "2")
    private int blindnessAmplifier;

    @ConfigInject(type = Double.class, path = "Gravity-Velocity", defaultValue = "-0.5")
    private double gravityVelocity;

    @ConfigInject(type = Double.class, path = "Gravity-Radius", defaultValue = "5.0")
    private double gravityRadius;

    @ConfigInject(type = Boolean.class, path = "Friendly-Fire", defaultValue = "true")
    private boolean friendlyFire;

    public GravityGrenade(final WeaponManager manager) {
        super(manager, new ItemStack(Material.STAINED_CLAY, 1, (byte) 15), ActionType.LEFT_CLICK);
    }

    @Override
    public int getModel() {
        return 942149;
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
                "Creates a field that disrupts all players caught inside.",
                "",
                UtilString.pair("<gray>Left-Click", "<yellow>Throw"),
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", UtilTime.getTime(this.recharge))),
                UtilString.pair("<gray>Duration", String.format("<green>%s", UtilTime.getTime(this.duration)))
        };
    }

    @Override
    public void onActivate(final Player player, final ActionType actionType) {
        final Throwable throwable = new Throwable(this.getAbilityName(), this.getItemStack(), player, this.duration, player.getLocation().getDirection().multiply(this.itemVelocity)) {
            @Override
            public double getCollideRadius() {
                return GravityGrenade.this.gravityRadius;
            }
        };

        this.getInstance(Core.class).getManagerByClass(ThrowableManager.class).addThrowable(throwable);

        UtilMessage.simpleMessage(player, "Item", "You threw a <var>.", Collections.singletonList(this.getDisplayName()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onThrowableGrounded(final ThrowableGroundedEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.isThrowableName(this.getAbilityName()))) {
            return;
        }

        final Location location = event.getLocation().clone();

        if (UtilServer.getEvent(new WeaponLocationEvent(this, location)).isCancelled()) {
            event.setCancelled(true);
            return;
        }

        event.setRemoveItem(true);

        final double increment = 2 * Math.PI / particleCount;

        for (int i = 0; i < particleCount; i++) {
            final double angle = i * increment;

            for (int j = 0; j < particleRadius; j++) {
                final double x = Math.cos(angle) * j;
                final double z = Math.sin(angle) * j;

                location.add(x, 0, z);
                location.getWorld().spigot().playEffect(location, Effect.LARGE_SMOKE, 0, 0, 0.0F, 0.0F, 0.0F, 0, 1, 100);
                location.subtract(x, 0, z);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onThrowableCollideEntity(final ThrowableCollideEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.isThrowableName(this.getAbilityName()))) {
            return;
        }

        final LivingEntity target = event.getTarget();

        if (UtilServer.getEvent(new WeaponLocationEvent(this, target.getLocation())).isCancelled()) {
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

        UtilEntity.givePotionEffect(target, PotionEffectType.BLINDNESS, this.blindnessAmplifier, this.blindnessDuration);

        final Vector vector = event.getThrowable().getItem().getLocation().toVector();

        vector.setY(vector.getY() + 1);

        target.setVelocity(target.getLocation().toVector().subtract(vector).normalize().multiply(this.gravityVelocity));

        if (target.getFallDistance() > 0.0F) {
            target.setFallDistance(0.0F);
        }
    }

    @Override
    public float getEnergy(final ActionType actionType) {
        return 0.0F;
    }

    @Override
    public long getRecharge(final ActionType actionType) {
        return this.recharge;
    }
}