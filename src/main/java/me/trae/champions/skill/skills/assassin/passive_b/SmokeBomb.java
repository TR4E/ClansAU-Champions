package me.trae.champions.skill.skills.assassin.passive_b;

import me.trae.api.champions.skill.events.SkillActivateEvent;
import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.api.damage.events.damage.CustomPreDamageEvent;
import me.trae.champions.role.types.Assassin;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.DropSkill;
import me.trae.champions.skill.types.interfaces.IActiveSkill;
import me.trae.core.Core;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.effect.EffectManager;
import me.trae.core.effect.data.EffectData;
import me.trae.core.effect.types.Invisibility;
import me.trae.core.updater.annotations.Update;
import me.trae.core.updater.interfaces.Updater;
import me.trae.core.utility.*;
import me.trae.core.utility.objects.SoundCreator;
import me.trae.core.weapon.events.WeaponActivateEvent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class SmokeBomb extends DropSkill<Assassin, SkillData> implements Listener, Updater {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "85.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "45_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "3_000")
    private long duration;

    @ConfigInject(type = Double.class, path = "Distance", defaultValue = "2.5")
    private double distance;

    @ConfigInject(type = Integer.class, path = "Blindness-Amplifier", defaultValue = "1")
    private int blindnessAmplifier;

    @ConfigInject(type = Long.class, path = "Blindness-Duration", defaultValue = "4_000")
    private long blindnessDuration;

    @ConfigInject(type = Boolean.class, path = "Arrow-Damage", defaultValue = "true")
    private boolean arrowDamage;

    @ConfigInject(type = Boolean.class, path = "Friendly-Fire", defaultValue = "false")
    private boolean friendlyFire;

    public SmokeBomb(final Assassin module) {
        super(module);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private long getDuration(final int level) {
        return this.duration + (level * 1000L);
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Instantly vanish before your foes for a ",
                String.format("maximum of <green>%s</green>", UtilTime.getTime(this.getDuration(level))),
                "hitting an enemy or using abilities",
                "will make you reappear.",
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

    @Override
    public void onActivate(final Player player, final int level) {
        final long duration = this.getDuration(level);

        this.addUser(new SkillData(player, level, duration));

        this.getInstance(Core.class).getManagerByClass(EffectManager.class).getModuleByClass(Invisibility.class).addUser(new EffectData(player, duration));

        for (int i = 0; i < 3; i++) {
            new SoundCreator(Sound.FIZZ, 2.0F, 0.5F).play(player.getLocation());
        }

        UtilParticle.playParticle(EnumParticle.EXPLOSION_HUGE, player.getLocation(), 0.0F, 1);

        for (final Player targetPlayer : UtilEntity.getNearbyEntities(Player.class, player.getLocation(), this.distance)) {
            if (targetPlayer == player) {
                continue;
            }

            final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, player, UtilJava.cast(Player.class, targetPlayer));
            UtilServer.callEvent(friendlyFireEvent);
            if (friendlyFireEvent.isCancelled()) {
                continue;
            }

            if (!(this.friendlyFire)) {
                if (!(friendlyFireEvent.isVulnerable())) {
                    continue;
                }
            }

            UtilEntity.givePotionEffect(targetPlayer, PotionEffectType.BLINDNESS, this.blindnessAmplifier, this.blindnessDuration);
        }

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @Override
    public void reset(final Player player) {
        if (!(this.isUserByPlayer(player))) {
            return;
        }

        final Invisibility effect = this.getInstance(Core.class).getManagerByClass(EffectManager.class).getModuleByClass(Invisibility.class);
        if (effect.isUserByEntity(player)) {
            effect.removeUser(player);
        }

        UtilMessage.message(player, this.getName(), "You have reappeared.");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCustomPreDamage(final CustomPreDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (this.arrowDamage) {
            if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE && event.getProjectile() instanceof Arrow) {
                return;
            }
        }

        if (!(event.getDamagee() instanceof Player)) {
            return;
        }

        final Player damagee = event.getDamageeByClass(Player.class);

        if (!(this.isUserByPlayer(damagee))) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCustomPostDamage(final CustomPostDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        final Player damager = event.getDamagerByClass(Player.class);

        if (!(this.isUserByPlayer(damager))) {
            return;
        }

        this.reset(damager);
        this.removeUser(damager);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSkillActivate(final SkillActivateEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getSkill() instanceof IActiveSkill)) {
            return;
        }

        if (event.getSkill() instanceof SmokeBomb) {
            return;
        }

        final Player player = event.getPlayer();

        if (!(this.isUserByPlayer(player))) {
            return;
        }

        this.reset(player);
        this.removeUser(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWeaponActivate(final WeaponActivateEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        if (!(this.isUserByPlayer(player))) {
            return;
        }

        this.reset(player);
        this.removeUser(player);
    }

    @Update(delay = 250L)
    public void onUpdater() {
        for (final Player player : this.getPlayers()) {
            for (int i = 0; i < 5; i++) {
                player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 4);
            }
        }
    }

    @Override
    public float getEnergy(final int level) {
        return this.energy;
    }

    @Override
    public long getRecharge(final int level) {
        return this.recharge;
    }
}