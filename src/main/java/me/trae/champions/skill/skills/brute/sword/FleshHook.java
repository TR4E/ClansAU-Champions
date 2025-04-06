package me.trae.champions.skill.skills.brute.sword;

import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.api.damage.utility.UtilDamage;
import me.trae.champions.role.types.Brute;
import me.trae.champions.skill.skills.brute.sword.data.FleshHookData;
import me.trae.champions.skill.types.ChannelSkill;
import me.trae.champions.skill.types.models.ToggleSkill;
import me.trae.core.Core;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.throwable.Throwable;
import me.trae.core.throwable.ThrowableManager;
import me.trae.core.throwable.events.ThrowableCollideEntityEvent;
import me.trae.core.throwable.events.ThrowableGroundedEvent;
import me.trae.core.utility.*;
import me.trae.core.utility.objects.SoundCreator;
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
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collections;

public class FleshHook extends ChannelSkill<Brute, FleshHookData> implements ToggleSkill<FleshHookData>, Listener {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "30.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "18_000")
    private long recharge;

    @ConfigInject(type = Integer.class, path = "Max-Charges", defaultValue = "100")
    private int maxCharges;

    @ConfigInject(type = Integer.class, path = "Incremented-Charges", defaultValue = "25")
    private int incrementedCharges;

    @ConfigInject(type = Long.class, path = "Per-Charge-Duration", defaultValue = "1_000")
    private long perChargeDuration;

    @ConfigInject(type = String.class, path = "Material", defaultValue = "TRIPWIRE_HOOK")
    private String material;

    @ConfigInject(type = Double.class, path = "Strength", defaultValue = "2.0")
    private double strength;

    @ConfigInject(type = Double.class, path = "yBase", defaultValue = "0.0")
    private double yBase;

    @ConfigInject(type = Double.class, path = "yAdd", defaultValue = "0.8")
    private double yAdd;

    @ConfigInject(type = Double.class, path = "yMax", defaultValue = "1.0")
    private double yMax;

    @ConfigInject(type = Boolean.class, path = "groundBoost", defaultValue = "true")
    private boolean groundBoost;

    public FleshHook(final Brute module) {
        super(module);
    }

    @Override
    public Class<FleshHookData> getClassOfData() {
        return FleshHookData.class;
    }

    private Material getMaterial() {
        try {
            return Material.valueOf(this.material);
        } catch (final Exception ignored) {
        }

        return Material.TRIPWIRE_HOOK;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Hold Block with a Sword to Channel.",
                "",
                "Fire a few hooks at opponents, pulling them towards you.",
                "",
                "Higher Chance Time = Faster Hook",
                "",
                UtilString.pair("<gray>Recharge", UtilString.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", UtilString.format("<green>%s", this.getEnergyUsingString(level)))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        this.addUser(new FleshHookData(player, level));

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You are now preparing <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @Override
    public void onUsing(final Player player, final FleshHookData data) {
        if (!(UtilTime.elapsed(data.getLastUpdated(), this.perChargeDuration))) {
            return;
        }

        if (data.getCharges() >= this.maxCharges) {
            return;
        }

        data.setCharges(data.getCharges() + this.incrementedCharges);

        data.updateLastUpdated();

        new SoundCreator(Sound.CLICK, 0.4F, 1.0F + (0.05F * data.getCharges())).play(player.getLocation());

        UtilActionBar.sendActionBar(player, 4, UtilString.pair(UtilString.format("<green><bold>%s", this.getName()), UtilString.format("<yellow>+%s%", data.getCharges())));
    }

    @Override
    public void onDeActivate(final Player player, final FleshHookData data) {
        final Location playerLocation = player.getLocation();
        final Location playerLocationCloned = playerLocation.clone();

        final double base = 0.8D;

        final double velocity = base + (data.getCharges() / 20.0D) * (0.25D * base);

        final ThrowableManager throwableManager = this.getInstanceByClass(Core.class).getManagerByClass(ThrowableManager.class);

        for (int i = -20; i <= 20; i += 5) {
            playerLocationCloned.setYaw(playerLocation.getYaw() + i);

            final Vector vector = playerLocationCloned.getDirection().multiply(velocity);

            final Throwable throwable = new Throwable(this.getDisplayName(data.getLevel()), new ItemStack(this.getMaterial()), player, 10_000L, player.getEyeLocation(), vector) {
                @Override
                public boolean requireCollideEntityOnItemGrounded() {
                    return false;
                }
            };

            throwableManager.addThrowable(throwable);
        }

        UtilActionBar.removeActionBar(player, 4);

        new SoundCreator(Sound.IRONGOLEM_THROW, 2.0F, 0.8F).play(player.getLocation());

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(data.getLevel())));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onThrowableCollideEntity(final ThrowableCollideEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Throwable throwable = event.getThrowable();

        if (!(throwable.getName().startsWith(this.getName()))) {
            return;
        }

        final Player throwerPlayer = throwable.getThrowerPlayer();

        final LivingEntity targetEntity = event.getTarget();

        if (throwable.isCollided(targetEntity) || this.getInstanceByClass(Core.class).getManagerByClass(ThrowableManager.class).getThrowableMap().values().stream().filter(t -> t.getName().startsWith(this.getName()) && t.getThrowerPlayer().equals(throwerPlayer)).anyMatch(t -> t.isCollided(targetEntity))) {
            return;
        }

        if (targetEntity instanceof Player) {
            final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, throwerPlayer, UtilJava.cast(Player.class, targetEntity));
            UtilServer.callEvent(friendlyFireEvent);
            if (friendlyFireEvent.isCancelled()) {
                return;
            }

            if (!(friendlyFireEvent.isVulnerable())) {
                return;
            }

            UtilMessage.simpleMessage(throwerPlayer, this.getModule().getName(), "You hit <var> with <green><var></green>.", Arrays.asList(friendlyFireEvent.getTargetName(), throwable.getName()));
            UtilMessage.simpleMessage(targetEntity, this.getModule().getName(), "<var> hit you with <green><var></green>.", Arrays.asList(friendlyFireEvent.getPlayerName(), throwable.getName()));
        } else {
            UtilMessage.simpleMessage(throwerPlayer, this.getModule().getName(), "You hit a <yellow><var></yellow> with <green><var></green>.", Arrays.asList(targetEntity.getName(), throwable.getName()));
        }

        UtilVelocity.velocity(targetEntity, UtilVelocity.getTrajectory(targetEntity.getLocation().toVector(), throwerPlayer.getLocation().toVector()), this.strength, this.yBase, this.yAdd, this.yMax, this.groundBoost);

        UtilDamage.damage(targetEntity, throwerPlayer, EntityDamageEvent.DamageCause.CUSTOM, 2.0D, throwable.getName(), 3000L);

        throwable.addCollided(targetEntity);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onThrowableGrounded(final ThrowableGroundedEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Throwable throwable = event.getThrowable();

        if (!(throwable.getName().startsWith(this.getName()))) {
            return;
        }

        event.setRemoveItem(true);
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 2;

        return this.energy - value;
    }

    @Override
    public float getEnergyNeeded(final int level) {
        return 0.0F;
    }

    @Override
    public float getEnergyUsing(final int level) {
        return 0.0F;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = (level - 1) * 2;

        return this.recharge - (value * 1000L);
    }
}