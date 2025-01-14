package me.trae.champions.skill.skills.assassin.bow;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.effect.EffectManager;
import me.trae.champions.effect.types.Vulnerable;
import me.trae.champions.role.types.Assassin;
import me.trae.champions.skill.types.ActiveBowSkill;
import me.trae.champions.skill.types.data.BowSkillData;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.effect.data.EffectData;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class MarkedForDeath extends ActiveBowSkill<Assassin, BowSkillData> {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "50.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "30_000")
    private long recharge;

    @ConfigInject(type = Integer.class, path = "Amplifier", defaultValue = "2")
    private int amplifier;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "7_000")
    private long duration;

    public MarkedForDeath(final Assassin module) {
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
                "Left-Click with a Bow to Prepare.",
                "",
                "Your next arrow will mark players for death,",
                String.format("giving them Vulnerability %s for %s", this.getValueString(Integer.class, this::getAmplifier, level), this.getValueString(Long.class, this::getDuration, level)),
                "Causing them to take 50% additional damage",
                "from all targets.",
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

        if (damagee instanceof Player) {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit <var> with <green><var></green>.", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel())));
            UtilMessage.simpleMessage(damagee, this.getModule().getName(), "<var> hit you with <green><var></green>.", Arrays.asList(event.getDamagerName(), this.getDisplayName(data.getLevel())));
        } else {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit a <var> with <green><var></green>.", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel())));
        }

        this.getInstance().getManagerByClass(EffectManager.class).getModuleByClass(Vulnerable.class).addUser(new EffectData(damageeLivingEntity, this.getAmplifier(data.getLevel()), duration));

        event.setReason(this.getDisplayName(data.getLevel()), duration);
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