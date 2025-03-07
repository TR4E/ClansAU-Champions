package me.trae.champions.skill.skills.ranger.sword;

import me.trae.api.damage.events.damage.CustomDamageEvent;
import me.trae.champions.role.types.Ranger;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.Core;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.effect.EffectManager;
import me.trae.core.effect.data.EffectData;
import me.trae.core.effect.types.NoFall;
import me.trae.core.utility.UtilEntity;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.UtilVelocity;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;

public class Disengage extends ActiveSkill<Ranger, SkillData> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "30.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "12_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Prepare-Duration", defaultValue = "1_000")
    private long prepareDuration;

    @ConfigInject(type = Long.class, path = "Slowness-Duration", defaultValue = "2_000")
    private long slownessDuration;

    @ConfigInject(type = Integer.class, path = "Slowness-Amplifier", defaultValue = "4")
    private int slownessAmplifier;

    @ConfigInject(type = Double.class, path = "Strength", defaultValue = "4.0")
    private double strength;

    @ConfigInject(type = Double.class, path = "yBase", defaultValue = "0.0")
    private double yBase;

    @ConfigInject(type = Double.class, path = "yAdd", defaultValue = "0.8")
    private double yAdd;

    @ConfigInject(type = Double.class, path = "yMax", defaultValue = "1.5")
    private double yMax;

    @ConfigInject(type = Boolean.class, path = "groundBoost", defaultValue = "true")
    private boolean groundBoost;


    public Disengage(final Ranger module) {
        super(module, ActiveSkillType.SWORD);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private long getSlownessDuration(final int level) {
        return this.slownessDuration + (level * 1000L);
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with a Sword to Activate.",
                "",
                UtilString.format("If you are attacked within %s", this.getValueString(Long.class, this.prepareDuration)),
                "you successfully disengage.",
                "",
                "If successful, you leap backwards",
                UtilString.format("and your attacker receives Slowness %s", this.getValueString(Integer.class, this.slownessAmplifier)),
                UtilString.format("for %s.", this.getValueString(Long.class, this::getSlownessDuration, level)),
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
        new SoundCreator(Sound.ZOMBIE_REMEDY, 2.0F, 2.0F).play(player.getLocation());

        this.addUser(new SkillData(player, level, this.prepareDuration));

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You have prepared to <green><var></green>", Collections.singletonList(this.getDisplayName(level)));
    }

    @Override
    public void onExpire(final Player player, final SkillData data) {
        new SoundCreator(Sound.NOTE_STICKS).play(player);

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You failed to <green><var></green>", Collections.singletonList(this.getDisplayName(data.getLevel())));
    }

    @EventHandler
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

        if (!(event.getDamager() instanceof LivingEntity)) {
            return;
        }

        final Player damagee = event.getDamageeByClass(Player.class);

        final SkillData data = this.getUserByPlayer(damagee);
        if (data == null) {
            return;
        }

        final LivingEntity damager = event.getDamagerByClass(LivingEntity.class);

        event.setKnockback(0.0D);

        event.setDamage(0.0D);

        this.removeUser(damagee);

        UtilEntity.givePotionEffect(damager, PotionEffectType.SLOW, this.slownessAmplifier, this.getSlownessDuration(data.getLevel()));

        this.getInstanceByClass(Core.class).getManagerByClass(EffectManager.class).getModuleByClass(NoFall.class).addUser(new EffectData(damagee, 3000L) {
            @Override
            public boolean isRemoveOnAction() {
                return true;
            }
        });

        UtilVelocity.velocity(damagee, damager.getLocation().getDirection(), this.strength, this.yBase, this.yAdd, this.yMax, this.groundBoost);

        UtilMessage.simpleMessage(damagee, this.getModule().getName(), "You used <green><var></green> against <var>.", Arrays.asList(this.getDisplayName(data.getLevel()), event.getDamagerName()));
        UtilMessage.simpleMessage(damager, this.getModule().getName(), "<var> used <green><var></green> against you.", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel())));
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 3;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = (int) ((level - 1) * 1.5);

        return this.recharge - (value * 1000L);
    }
}