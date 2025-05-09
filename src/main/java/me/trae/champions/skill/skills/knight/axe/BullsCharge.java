package me.trae.champions.skill.skills.knight.axe;

import me.trae.api.damage.events.CustomKnockbackEvent;
import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Knight;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.UtilEntity;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;

public class BullsCharge extends ActiveSkill<Knight, SkillData> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "35.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "12_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "4_000")
    private long duration;

    public BullsCharge(final Knight module) {
        super(module, ActiveSkillType.AXE);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private int getAmplifier(final int level) {
        return level;
    }

    private long getDuration(final int level) {
        return this.duration + ((level - 1) * 1000L);
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with an Axe to Activate.",
                "",
                "Enter a rage, gaining massive movement speed",
                UtilString.format("and slowing anything you hit for %s.", this.getValueString(Long.class, this::getDuration, level)),
                "",
                "While charging, you take no knockback.",
                "",
                UtilString.pair("<gray>Recharge", UtilString.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", UtilString.format("<green>%s", this.getEnergyString(level)))
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
        player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, Material.OBSIDIAN);

        new SoundCreator(Sound.ENDERMAN_SCREAM, 1.5F, 0.0F).play(player.getLocation());

        final long duration = this.getDuration(level);

        UtilEntity.givePotionEffect(player, PotionEffectType.SPEED, this.getAmplifier(level), duration);

        this.addUser(new SkillData(player, level, duration));

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @Override
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            UtilEntity.removePotionEffect(player, PotionEffectType.SPEED);
        }

        super.reset(player);
    }

    @Override
    public void onExpire(final Player player, final SkillData data) {
        new SoundCreator(Sound.NOTE_STICKS).play(player);

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You failed <green><var></green>.", Collections.singletonList(this.getDisplayName(data.getLevel())));
    }

    @EventHandler
    public void onCustomKnockback(final CustomKnockbackEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final CustomPostDamageEvent damageEvent = event.getDamageEvent();

        if (damageEvent.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        if (!(damageEvent.getDamagee() instanceof Player)) {
            return;
        }

        if (!(damageEvent.getDamager() instanceof LivingEntity)) {
            return;
        }

        final Player damagee = damageEvent.getDamageeByClass(Player.class);

        if (!(this.isUserByPlayer(damagee))) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCustomPostDamage(final CustomPostDamageEvent event) {
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

        final Player damager = event.getDamagerByClass(Player.class);

        final SkillData data = this.getUserByPlayer(damager);
        if (data == null) {
            return;
        }

        final LivingEntity damagee = event.getDamageeByClass(LivingEntity.class);

        final long duration = this.getDuration(data.getLevel());

        UtilEntity.givePotionEffect(damagee, PotionEffectType.SLOW, this.getAmplifier(data.getLevel()), duration);

        new SoundCreator(Sound.ENDERMAN_SCREAM, 1.5F, 0.0F).play(damager.getLocation());
        new SoundCreator(Sound.ZOMBIE_METAL, 1.5F, 0.5F).play(damagee.getLocation());

        if (damagee instanceof Player) {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit <var> with <green><var></green>.", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel())));
            UtilMessage.simpleMessage(damagee, this.getModule().getName(), "<var> hit you with <green><var></green>.", Arrays.asList(event.getDamagerName(), this.getDisplayName(data.getLevel())));
        } else {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit a <var> with <green><var></green>.", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel())));
        }

        event.setReason(this.getDisplayName(data.getLevel()), duration);

        this.reset(damager);
        this.removeUser(damager);
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 5;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = (level - 1) * 2;

        return this.recharge - (value * 1000L);
    }
}