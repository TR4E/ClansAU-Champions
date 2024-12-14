package me.trae.champions.skill.skills.assassin.bow;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Assassin;
import me.trae.champions.skill.types.ActiveBowSkill;
import me.trae.champions.skill.types.data.BowSkillData;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class ToxicArrow extends ActiveBowSkill<Assassin, BowSkillData> {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "30.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "14_000")
    private long recharge;

    @ConfigInject(type = Integer.class, path = "Amplifier", defaultValue = "1")
    private int amplifier;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "15_000")
    private long duration;

    public ToxicArrow(final Assassin module) {
        super(module);
    }

    @Override
    public Class<BowSkillData> getClassOfData() {
        return BowSkillData.class;
    }

    private int getAmplifier(final int level) {
        return this.amplifier;
    }

    private long getDuration(final int level) {
        return this.duration + (level * 1000L);
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Left-Click with a Bow to Prepare",
                "",
                "Your next arrow will give your",
                String.format("target confusion for <green>%s</green>.", UtilTime.getTime(this.getDuration(level))),
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyString(level)))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        this.addUser(new BowSkillData(player, level));

        super.onActivate(player, level);
    }

    @Override
    public void onHitByEntity(final Player damager, final Entity damagee, final CustomPostDamageEvent event, final BowSkillData data) {
        if (!(damagee instanceof LivingEntity)) {
            return;
        }

        final LivingEntity damageeLivingEntity = UtilJava.cast(LivingEntity.class, damagee);

        final long duration = this.getDuration(data.getLevel());

        UtilEntity.givePotionEffect(damageeLivingEntity, PotionEffectType.CONFUSION, this.getAmplifier(data.getLevel()), duration);

        if (damagee instanceof Player) {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit <var> with <green><var></green>.", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel())));
            UtilMessage.simpleMessage(damagee, this.getModule().getName(), "<var> hit you with <green><var></green>.", Arrays.asList(event.getDamagerName(), this.getDisplayName(data.getLevel())));
        } else {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit a <var> with <green><var></green>.", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel())));
        }

        event.setReason(this.getDisplayName(data.getLevel()), duration);
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 2;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = level - 1;

        return this.recharge - (value * 1000L);
    }
}