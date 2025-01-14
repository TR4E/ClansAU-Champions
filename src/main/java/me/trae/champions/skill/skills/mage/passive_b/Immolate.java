package me.trae.champions.skill.skills.mage.passive_b;

import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.api.champions.skill.events.SkillLocationEvent;
import me.trae.api.damage.utility.UtilDamage;
import me.trae.champions.role.types.Mage;
import me.trae.champions.skill.data.types.ToggleUpdaterDropSkillData;
import me.trae.champions.skill.types.ToggleUpdaterDropSkill;
import me.trae.core.Core;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.effect.EffectManager;
import me.trae.core.effect.types.FireResistance;
import me.trae.core.throwable.Throwable;
import me.trae.core.throwable.ThrowableManager;
import me.trae.core.throwable.events.ThrowableCollideEntityEvent;
import me.trae.core.utility.UtilEntity;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilServer;
import me.trae.core.utility.UtilString;
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
import org.bukkit.util.Vector;

public class Immolate extends ToggleUpdaterDropSkill<Mage, ToggleUpdaterDropSkillData> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "4.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "5_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "5_000")
    private long duration;

    @ConfigInject(type = Integer.class, path = "Effect-Amplifier", defaultValue = "2")
    private int effectAmplifier;

    @ConfigInject(type = Long.class, path = "Fire-Duration", defaultValue = "4_000")
    private long fireDuration;

    @ConfigInject(type = Boolean.class, path = "Friendly-Fire", defaultValue = "false")
    private boolean friendlyFire;

    @ConfigInject(type = String.class, path = "Material", defaultValue = "BLAZE_POWDER")
    private String material;

    public Immolate(final Mage module) {
        super(module);
    }

    @Override
    public Class<ToggleUpdaterDropSkillData> getClassOfData() {
        return ToggleUpdaterDropSkillData.class;
    }

    private Material getMaterial() {
        try {
            return Material.valueOf(this.material);
        } catch (final Exception ignored) {
        }

        return Material.BLAZE_POWDER;
    }

    private long getDuration(final int level) {
        return this.duration;
    }

    @Override
    public String[] getDescription(final int level) {
        final String effectAmplifierString = this.getValueString(Integer.class, this.effectAmplifier);

        return new String[]{
                "Drop Sword/Axe to Toggle.",
                "",
                "Igniting yourself in flaming fury.",
                String.format("You receive Speed %s and Fire Resistance %s", effectAmplifierString, effectAmplifierString),
                "",
                "You leave a trail of fire, which",
                "burns players that go near it.",
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyUsingString(level)))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        this.addUser(new ToggleUpdaterDropSkillData(player, level));

        UtilMessage.simpleMessage(player, this.getModule().getName(), UtilString.pair(this.getName(), "<green>On"));
    }

    @Override
    public void onDeActivate(final Player player, final ToggleUpdaterDropSkillData data) {
        UtilMessage.simpleMessage(player, this.getModule().getName(), UtilString.pair(this.getName(), "<red>Off"));
    }

    @Override
    public void onUsing(final Player player, final ToggleUpdaterDropSkillData data) {
        final Throwable throwable = new Throwable(this.getDisplayName(data.getLevel()), new ItemStack(this.getMaterial()), player, this.getDuration(data.getLevel()), player.getLocation(), new Vector((Math.random() - 0.5D) / 3.0D, Math.random() / 3.0D, (Math.random() - 0.5D) / 3.0D));

        this.getInstanceByClass(Core.class).getManagerByClass(ThrowableManager.class).addThrowable(throwable);

        new SoundCreator(Sound.FIZZ, 0.3F, 0.0F).play(player.getLocation());

        UtilEntity.givePotionEffect(player, PotionEffectType.SPEED, this.effectAmplifier, Integer.MAX_VALUE);
        UtilEntity.givePotionEffect(player, PotionEffectType.FIRE_RESISTANCE, this.effectAmplifier, Integer.MAX_VALUE);
    }

    @Override
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            UtilEntity.removePotionEffect(player, PotionEffectType.SPEED);
            UtilEntity.removePotionEffect(player, PotionEffectType.FIRE_RESISTANCE);
        }

        super.reset(player);
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

        if (this.getInstanceByClass(Core.class).getManagerByClass(EffectManager.class).getModuleByClass(FireResistance.class).isUserByEntity(targetEntity)) {
            return;
        }

        if (UtilServer.getEvent(new SkillLocationEvent(this, targetEntity.getLocation())).isCancelled()) {
            return;
        }

        if (targetEntity instanceof Player) {
            final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, throwerPlayer, event.getTargetByClass(Player.class));
            UtilServer.callEvent(friendlyFireEvent);
            if (friendlyFireEvent.isCancelled()) {
                return;
            }

            if (!(this.friendlyFire)) {
                if (targetEntity == throwerPlayer) {
                    return;
                }

                if (!(friendlyFireEvent.isVulnerable())) {
                    return;
                }
            }
        }

        if (throwable.isCollided(targetEntity)) {
            return;
        }

        UtilDamage.damage(targetEntity, throwerPlayer, EntityDamageEvent.DamageCause.CUSTOM, 1.0D, throwable.getName(), 1000L);

        targetEntity.setFireTicks((int) (this.fireDuration / 50L));

        throwable.addCollided(targetEntity, 500L);
    }

    @Override
    public float getEnergy(final int level) {
        return 0.0F;
    }

    @Override
    public float getEnergyNeeded(final int level) {
        return this.energy;
    }

    @Override
    public float getEnergyUsing(final int level) {
        return this.getEnergyNeeded(level) / 2;
    }

    @Override
    public long getRecharge(final int level) {
        return this.recharge;
    }
}