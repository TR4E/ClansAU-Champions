package me.trae.champions.skill.skills.mage.axe;

import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.api.champions.skill.events.SkillLocationEvent;
import me.trae.api.damage.utility.UtilDamage;
import me.trae.champions.effect.EffectManager;
import me.trae.champions.effect.types.Shock;
import me.trae.champions.role.types.Mage;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.Core;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.effect.data.EffectData;
import me.trae.core.throwable.Throwable;
import me.trae.core.throwable.ThrowableManager;
import me.trae.core.throwable.events.ThrowableCollideEntityEvent;
import me.trae.core.utility.*;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;

public class LightningOrb extends ActiveSkill<Mage, SkillData> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "30.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "25_000")
    private long recharge;

    @ConfigInject(type = Double.class, path = "Item-Velocity", defaultValue = "0.7")
    private double itemVelocity;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "5_000")
    private long duration;

    @ConfigInject(type = Long.class, path = "Shock-Duration", defaultValue = "2_000")
    private long shockDuration;

    @ConfigInject(type = Double.class, path = "Damage", defaultValue = "11.0")
    private double damage;

    @ConfigInject(type = Integer.class, path = "Distance", defaultValue = "3")
    private int distance;

    @ConfigInject(type = Integer.class, path = "Slowness-Amplifier", defaultValue = "2")
    private int slownessAmplifier;

    @ConfigInject(type = Long.class, path = "Slowness-Duration", defaultValue = "4_000")
    private long slownessDuration;

    @ConfigInject(type = String.class, path = "Material", defaultValue = "DIAMOND_BLOCK")
    private String material;

    public LightningOrb(final Mage module) {
        super(module, ActiveSkillType.AXE);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private long getDuration(final int level) {
        return this.duration;
    }

    private int getDistance(final int level) {
        return this.distance + (level / 2);
    }

    private int getSlownessAmplifier(final int level) {
        return this.slownessAmplifier;
    }

    private long getSlownessDuration(final int level) {
        return this.slownessDuration;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with a Sword to Activate.",
                "",
                "Launches a lightning orb. Directly hitting a player",
                String.format("will strike them within <green>%s</green> blocks", this.getDistance(level)),
                String.format("with lightning, giving them Slowness %s for %s", this.getSlownessAmplifier(level), UtilTime.getTime(this.getSlownessDuration(level))),
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyString(level)))
        };
    }

    @Override
    public int getDefaultLevel() {
        return 2;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    private Material getMaterial() {
        try {
            return Material.valueOf(this.material);
        } catch (final Exception ignored) {
        }

        return Material.DIAMOND_BLOCK;
    }

    @Override
    public void onActivate(final Player player, final int level) {
        final long duration = this.getDuration(level);

        final Throwable throwable = new Throwable(this.getName(), new ItemStack(this.getMaterial()), player, duration, player.getEyeLocation(), player.getEyeLocation().getDirection().multiply(this.itemVelocity)) {
            @Override
            public double getCollideRadius() {
                return LightningOrb.this.getDistance(level);
            }
        };

        this.getInstance(Core.class).getManagerByClass(ThrowableManager.class).addThrowable(throwable);

        this.addUser(new SkillData(player, level, duration));

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onThrowableCollideEntity(final ThrowableCollideEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.isThrowableName(this.getName()))) {
            return;
        }

        final Throwable throwable = event.getThrowable();

        throwable.getItem().remove();

        if (throwable.getCollided().size() >= 3) {
            return;
        }

        final Player throwerPlayer = throwable.getThrowerPlayer();

        final SkillData data = this.getUserByPlayer(throwerPlayer);
        if (data == null) {
            return;
        }

        final LivingEntity targetEntity = event.getTarget();

        if (throwable.isCollided(targetEntity)) {
            return;
        }

        if (UtilServer.getEvent(new SkillLocationEvent(this, targetEntity.getLocation())).isCancelled()) {
            return;
        }

        if (targetEntity instanceof Player) {
            final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, throwerPlayer, event.getTargetByClass(Player.class));
            UtilServer.callEvent(friendlyFireEvent);
            if (friendlyFireEvent.isCancelled() || !(friendlyFireEvent.isVulnerable())) {
                return;
            }

            UtilMessage.simpleMessage(throwerPlayer, this.getModule().getName(), "You hit <var> with <green><var></green>.", Arrays.asList(friendlyFireEvent.getTargetName(), this.getDisplayName(data.getLevel())));
            UtilMessage.simpleMessage(targetEntity, this.getModule().getName(), "<var> hit you with <green><var></green>.", Arrays.asList(friendlyFireEvent.getPlayerName(), this.getDisplayName(data.getLevel())));
        } else {
            UtilMessage.simpleMessage(throwerPlayer, this.getModule().getName(), "You hit a <var> with <green><var></green>.", Arrays.asList(targetEntity.getName(), this.getDisplayName(data.getLevel())));
        }

        this.getInstance().getManagerByClass(EffectManager.class).getModuleByClass(Shock.class).addUser(new EffectData(targetEntity, this.shockDuration));

        UtilEntity.givePotionEffect(targetEntity, PotionEffectType.SLOW, this.getSlownessAmplifier(data.getLevel()), this.getSlownessDuration(data.getLevel()));

        targetEntity.getWorld().strikeLightningEffect(targetEntity.getLocation());

        new SoundCreator(Sound.AMBIENCE_THUNDER).play(targetEntity.getLocation());

        UtilDamage.damage(targetEntity, throwerPlayer, EntityDamageEvent.DamageCause.CUSTOM, this.damage, this.getDisplayName(data.getLevel()), this.shockDuration);

        throwable.addCollided(targetEntity);
    }

    @Override
    public float getEnergy(final int level) {
        final int value = level * 2;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = level * 2;

        return this.recharge - (value * 1000L);
    }
}