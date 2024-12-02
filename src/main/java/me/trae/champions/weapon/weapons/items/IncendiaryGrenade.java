package me.trae.champions.weapon.weapons.items;

import me.trae.api.damage.utility.UtilDamage;
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
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Random;

public class IncendiaryGrenade extends ActiveCustomItem<Champions, WeaponManager, WeaponData> implements Listener {

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "10_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "5_000")
    private long duration;

    @ConfigInject(type = Double.class, path = "Item-Velocity", defaultValue = "1.3")
    private double itemVelocity;

    @ConfigInject(type = Double.class, path = "Damage", defaultValue = "2.0")
    private double damage;

    @ConfigInject(type = Integer.class, path = "Particle-Count", defaultValue = "16")
    private int particleCount;

    @ConfigInject(type = Long.class, path = "Fire-Duration", defaultValue = "3_000")
    private long fireDuration;

    @ConfigInject(type = Double.class, path = "Radius", defaultValue = "3.0")
    private double radius;

    @ConfigInject(type = Boolean.class, path = "Friendly-Fire", defaultValue = "true")
    private boolean friendlyFire;

    public IncendiaryGrenade(final WeaponManager manager) {
        super(manager, new ItemStack(Material.MAGMA_CREAM), ActionType.LEFT_CLICK);
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
                "Burns people who enter the blast area.",
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
                return IncendiaryGrenade.this.radius;
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

        final Location location = event.getLocation();

        if (UtilServer.getEvent(new WeaponLocationEvent(this, location)).isCancelled()) {
            event.setCancelled(true);
            return;
        }

        event.setRemoveItem(true);

        final Random random = new Random();

        for (int i = 0; i < this.particleCount; i++) {
            UtilParticle.playParticle(
                    EnumParticle.FLAME,
                    location,
                    (float) location.getX() + 0.5F,
                    (float) (location.getY() + random.nextDouble() * 2.0F),
                    (float) location.getZ(), (float) random.nextGaussian(),
                    0.0F,
                    (float) random.nextGaussian(), 0.1F, 1
            );
        }

        new SoundCreator(Sound.DIG_GRASS, 0.6F, 0.0F).play(location);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onThrowableCollideEntity(final ThrowableCollideEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.isThrowableName(this.getAbilityName()))) {
            return;
        }

        final Player throwerPlayer = event.getThrowable().getThrowerPlayer();

        final LivingEntity targetEntity = event.getTarget();

        if (targetEntity instanceof Player) {
            final WeaponFriendlyFireEvent weaponFriendlyFireEvent = new WeaponFriendlyFireEvent(this, throwerPlayer, event.getTargetByClass(Player.class));
            UtilServer.callEvent(weaponFriendlyFireEvent);
            if (weaponFriendlyFireEvent.isCancelled()) {
                return;
            }

            if (!(this.friendlyFire)) {
                if (targetEntity == throwerPlayer) {
                    return;
                }

                if (!(weaponFriendlyFireEvent.isVulnerable())) {
                    return;
                }
            }
        }

        targetEntity.setFireTicks((int) (this.fireDuration / 50));

        UtilDamage.damage(targetEntity, throwerPlayer, EntityDamageEvent.DamageCause.CUSTOM, this.damage, this.getDisplayName(), this.fireDuration);
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