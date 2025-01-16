package me.trae.champions.skill.skills.brute.passive_b;

import me.trae.api.champions.skill.events.SkillLocationEvent;
import me.trae.api.damage.events.damage.CustomDamageEvent;
import me.trae.champions.role.types.Brute;
import me.trae.champions.skill.skills.brute.passive_b.data.StampedeData;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.gamer.Gamer;
import me.trae.core.gamer.GamerManager;
import me.trae.core.updater.annotations.Update;
import me.trae.core.updater.interfaces.Updater;
import me.trae.core.utility.*;
import me.trae.core.utility.injectors.annotations.Inject;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class Stampede extends PassiveSkill<Brute, StampedeData> implements Listener, Updater {

    @Inject
    private GamerManager gamerManager;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "5_000")
    private long duration;

    @ConfigInject(type = Long.class, path = "Last-Damaged-Duration", defaultValue = "2_000")
    private long lastDamagedDuration;

    @ConfigInject(type = Double.class, path = "Damage-Multiplier", defaultValue = "1.0")
    private double damageMultiplier;

    @ConfigInject(type = Double.class, path = "Knockback", defaultValue = "1.0")
    private double knockback;

    public Stampede(final Brute module) {
        super(module, PassiveSkillType.PASSIVE_B);
    }

    @Override
    public Class<StampedeData> getClassOfData() {
        return StampedeData.class;
    }

    private int getMaxAmplifier(final int level) {
        return level;
    }

    private long getDuration(final int level) {
        return this.duration;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "You slowly build up speed as you",
                "sprint. You gain a level of Speed",
                String.format("for every %s,", this.getValueString(Long.class, this::getDuration, level)),
                String.format("up to max of Speed %s.", this.getValueString(Integer.class, this::getMaxAmplifier, level)),
                "",
                "Attacking during stampede deals",
                String.format("%s bonus damage per speed level", this.damageMultiplier)
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

    @Override
    public boolean canActivate(final Player player) {
        if (!(player.isSprinting())) {
            return false;
        }

        if (UtilBlock.isInLiquid(player.getLocation())) {
            return false;
        }

        if (UtilServer.getEvent(new SkillLocationEvent(this, player.getLocation())).isCancelled()) {
            return false;
        }

        return true;
    }

    @Override
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            UtilEntity.removePotionEffect(player, PotionEffectType.SPEED);
        }

        super.reset(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCustomDamage(final CustomDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        if (!(event.getDamagee() instanceof LivingEntity)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        final LivingEntity damagee = event.getDamageeByClass(LivingEntity.class);
        final Player damager = event.getDamagerByClass(Player.class);

        final StampedeData data = this.getUserByPlayer(damager);
        if (data == null) {
            return;
        }

        if (!(data.hasAmplifier())) {
            return;
        }

        if (!(this.canActivate(damager))) {
            return;
        }

        final double damage = data.getAmplifier() * this.damageMultiplier;

        event.setDamage(event.getDamage() + damage);

        if (this.knockback > 0.0D) {
            event.setKnockback(this.knockback);
        }

        event.setReason(this.getDisplayName(data.getLevel()), 1000L);

        new SoundCreator(Sound.ZOMBIE_METAL, 1.0F, 1.5F).play(damagee.getLocation());

        final String damageString = String.format("<white>+%s dmg", damage);

        if (damagee instanceof Player) {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit <var> with <green><var></green> (<var><reset>).", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel()), damageString));
            UtilMessage.simpleMessage(damagee, this.getModule().getName(), "<var> hit you with <green><var></green> (<var><reset>).", Arrays.asList(event.getDamagerName(), this.getDisplayName(data.getLevel()), damageString));
        } else {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit a <var> with <green><var></green> (<var><reset>).", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel()), damageString));
        }

        this.reset(damager);
        this.removeUser(damager);
    }

    @Update(delay = 250L)
    public void onUpdater() {
        for (final Player player : this.getModule().getPlayers()) {
            if (!(this.canActivate(player))) {
                this.reset(player);
                this.removeUser(player);
                continue;
            }

            final int level = this.getLevel(player);
            if (level == 0) {
                this.reset(player);
                this.removeUser(player);
                continue;
            }

            final Gamer gamer = this.gamerManager.getGamerByPlayer(player);
            if (gamer == null) {
                this.reset(player);
                this.removeUser(player);
                continue;
            }

            if (!(gamer.hasLastDamaged(this.lastDamagedDuration))) {
                continue;
            }

            if (!(this.isUserByPlayer(player))) {
                this.addUser(new StampedeData(player, level));
            }

            final StampedeData data = this.getUserByPlayer(player);

            if (data.getLevel() != level) {
                data.setLevel(level);
            }

            if (data.getAmplifier() >= this.getMaxAmplifier(level)) {
                continue;
            }

            if (!(UtilTime.elapsed(data.getLastUpdated(), this.getDuration(level)))) {
                continue;
            }

            data.updateLastUpdated();

            data.setAmplifier(data.getAmplifier() + 1);

            new SoundCreator(Sound.ZOMBIE_IDLE, 2.0F, 0.2F * data.getAmplifier() + 1.0F).play(player.getLocation());

            UtilEntity.givePotionEffect(player, PotionEffectType.SPEED, data.getAmplifier(), Integer.MAX_VALUE);

            UtilMessage.simpleMessage(player, this.getName(), UtilString.pair("Speed", String.format("<yellow>%s", data.getAmplifier())));
        }
    }
}