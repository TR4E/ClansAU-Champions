package me.trae.champions.skill.skills.brute.sword;

import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.api.damage.utility.UtilDamage;
import me.trae.champions.role.types.Brute;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.updater.annotations.Update;
import me.trae.core.updater.interfaces.Updater;
import me.trae.core.utility.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;

public class Takedown extends ActiveSkill<Brute, SkillData> implements Updater {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "40.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "25_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Expire-Duration", defaultValue = "750")
    private long expireDuration;

    @ConfigInject(type = Double.class, path = "Damage", defaultValue = "10.0")
    private double damage;

    @ConfigInject(type = Double.class, path = "Distance", defaultValue = "1.0")
    private double distance;

    @ConfigInject(type = Integer.class, path = "Slowness-Amplifier", defaultValue = "4")
    private int slownessAmplifier;

    @ConfigInject(type = Double.class, path = "Strength", defaultValue = "1.8")
    private double strength;

    @ConfigInject(type = Double.class, path = "yBase", defaultValue = "0.0")
    private double yBase;

    @ConfigInject(type = Double.class, path = "yAdd", defaultValue = "0.4")
    private double yAdd;

    @ConfigInject(type = Double.class, path = "yMax", defaultValue = "0.6")
    private double yMax;

    @ConfigInject(type = Boolean.class, path = "groundBoost", defaultValue = "false")
    private boolean groundBoost;

    public Takedown(final Brute module) {
        super(module, ActiveSkillType.SWORD);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private int getSlownessAmplifier(final int level) {
        return this.slownessAmplifier;
    }

    private long getSlownessDuration(final int level) {
        return (1 + (level / 2)) * 1000L;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with a Sword to Activate.",
                "",
                "Hurl yourself towards an opponent.",
                "If you collide with them, you both",
                String.format("take damage and receive Slow %s", this.getSlownessAmplifier(level)),
                String.format("for <green>%s</green>.", UtilTime.getTime(this.getSlownessDuration(level))),
                "",
                "Cannot be used while grounded.",
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyString(level)))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        UtilVelocity.velocity(player, player.getLocation().getDirection(), this.strength, this.yBase, this.yAdd, this.yMax, this.groundBoost);

        this.addUser(new SkillData(player, level));

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @Override
    public boolean canActivate(final Player player) {
        if (!(super.canActivate(player))) {
            return false;
        }

        if (UtilBlock.isGrounded(player.getLocation())) {
            UtilMessage.simpleMessage(player, "Skill", "You cannot use <green><var></green> while grounded.", Collections.singletonList(this.getName()));
            return false;
        }

        return true;
    }

    @Update(delay = 100L)
    public void onUpdater() {
        this.getUsers().values().removeIf(data -> {
            final Player player = Bukkit.getPlayer(data.getUUID());
            if (player != null) {
                for (final LivingEntity targetEntity : UtilEntity.getNearbyEntities(LivingEntity.class, player.getLocation(), this.distance)) {
                    if (targetEntity instanceof Player) {
                        final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, player, UtilJava.cast(Player.class, targetEntity));
                        UtilServer.callEvent(friendlyFireEvent);
                        if (friendlyFireEvent.isCancelled() || !(friendlyFireEvent.isVulnerable())) {
                            continue;
                        }

                        UtilMessage.simpleMessage(player, this.getModule().getName(), "You hit <var> with <green><var></green>.", Arrays.asList(friendlyFireEvent.getTargetName(), this.getDisplayName(data.getLevel())));
                        UtilMessage.simpleMessage(targetEntity, this.getModule().getName(), "<var> hit you with <green><var></green>.", Arrays.asList(friendlyFireEvent.getPlayerName(), this.getDisplayName(data.getLevel())));
                    } else {
                        UtilMessage.simpleMessage(player, this.getModule().getName(), "You hit a <yellow><var></yellow> with <green><var></green>.", Arrays.asList(targetEntity.getName(), this.getDisplayName(data.getLevel())));
                    }

                    UtilDamage.damage(targetEntity, player, EntityDamageEvent.DamageCause.CUSTOM, this.damage, this.getDisplayName(data.getLevel()), 1000L);
                    UtilDamage.damage(player, targetEntity, EntityDamageEvent.DamageCause.CUSTOM, this.damage, UtilString.format("%s Recoil", this.getDisplayName(data.getLevel())), 1000L);

                    for (final LivingEntity receiver : Arrays.asList(player, targetEntity)) {
                        UtilEntity.givePotionEffect(receiver, PotionEffectType.SLOW, this.getSlownessAmplifier(data.getLevel()), this.getSlownessDuration(data.getLevel()));
                    }

                    return true;
                }

                if (UtilBlock.isGrounded(player.getLocation()) && UtilTime.elapsed(data.getSystemTime(), this.expireDuration)) {
                    return true;
                }
            }

            return false;
        });
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 3;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = (level - 1) * 2;

        return this.recharge - (value * 1000L);
    }
}