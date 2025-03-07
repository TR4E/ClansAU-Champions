package me.trae.champions.skill.skills.brute.passive_b;

import me.trae.api.champions.skill.events.SkillLocationEvent;
import me.trae.api.damage.events.damage.CustomDamageEvent;
import me.trae.api.death.events.CustomDeathEvent;
import me.trae.champions.role.types.Brute;
import me.trae.champions.skill.skills.brute.passive_b.data.BloodlustData;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.*;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class Bloodlust extends PassiveSkill<Brute, BloodlustData> implements Listener {

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "8_000")
    private long duration;

    @ConfigInject(type = Long.class, path = "Effect-Duration", defaultValue = "5_000")
    private long effectDuration;

    @ConfigInject(type = Integer.class, path = "Distance", defaultValue = "16")
    private int distance;

    public Bloodlust(final Brute module) {
        super(module, PassiveSkillType.PASSIVE_B);
    }

    @Override
    public Class<BloodlustData> getClassOfData() {
        return BloodlustData.class;
    }

    private int getMaxAmplifier(final int level) {
        return level;
    }

    private int getAmplifierPerIncrease() {
        return 1;
    }

    private long getDuration(final int level) {
        return this.duration;
    }

    private long getEffectDuration(final int level) {
        return this.effectDuration + (level * 1000L);
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "When an enemy dies within 15 blocks,",
                "you go into a Bloodlust, receiving",
                UtilString.format("Speed %s and Strength %s for %s.", this.getValueString(Integer.class, this.getAmplifierPerIncrease()), this.getValueString(Integer.class, this.getAmplifierPerIncrease()), this.getValueString(Long.class, this::getEffectDuration, level)),
                "",
                UtilString.format("Bloodlust can stack up to %s", this.getValueString(Integer.class, this::getMaxAmplifier, level)),
                "boosting the level of Speed and Strength."
        };
    }

    @Override
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            UtilEntity.removePotionEffect(player, PotionEffectType.SPEED);
            UtilEntity.removePotionEffect(player, PotionEffectType.INCREASE_DAMAGE);
        }

        super.reset(player);
    }

    @Override
    public void onExpire(final Player player, final BloodlustData data) {
        new SoundCreator(Sound.NOTE_STICKS).play(player);

        UtilMessage.simpleMessage(player, this.getModule().getName(), "<green><var></green> has ended.", Collections.singletonList(this.getDisplayName(data.getLevel())));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCustomDeath(final CustomDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        for (final Player player : UtilEntity.getNearbyEntities(Player.class, event.getEntity().getLocation(), this.distance)) {
            if (UtilServer.getEvent(new SkillLocationEvent(this, player.getLocation())).isCancelled()) {
                continue;
            }

            final int level = getLevel(player);
            if (level == 0) {
                continue;
            }

            if (!(this.isUserByPlayer(player))) {
                this.addUser(new BloodlustData(player, level));
            }

            final BloodlustData data = this.getUserByPlayer(player);

            data.setDuration(this.getDuration(data.getLevel()));

            data.setAmplifier(UtilMath.getMinAndMax(Integer.class, 0, this.getMaxAmplifier(data.getLevel()), data.getAmplifier() + this.getAmplifierPerIncrease()));

            UtilEntity.givePotionEffect(player, PotionEffectType.SPEED, data.getAmplifier(), this.getEffectDuration(data.getLevel()));
            UtilEntity.givePotionEffect(player, PotionEffectType.INCREASE_DAMAGE, data.getAmplifier(), this.getEffectDuration(data.getLevel()));

            UtilMessage.simpleMessage(player, this.getModule().getName(), "You entered Bloodlust at Level: <yellow><var>", Collections.singletonList(String.valueOf(data.getAmplifier())));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCustomDamage(final CustomDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        if (!(event.getDamagee() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        final Player damager = event.getDamagerByClass(Player.class);

        final BloodlustData data = this.getUserByPlayer(damager);
        if (data == null) {
            return;
        }

        if (data.hasExpired()) {
            return;
        }

        event.setReason(this.getDisplayName(data.getLevel()), 1000L);
    }
}