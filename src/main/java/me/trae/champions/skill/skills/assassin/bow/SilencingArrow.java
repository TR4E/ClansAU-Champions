package me.trae.champions.skill.skills.assassin.bow;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.effect.EffectManager;
import me.trae.champions.effect.types.Silenced;
import me.trae.champions.role.types.Assassin;
import me.trae.champions.skill.types.ActiveBowSkill;
import me.trae.champions.skill.types.data.BowSkillData;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.effect.data.EffectData;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.particle.ParticleEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class SilencingArrow extends ActiveBowSkill<Assassin, BowSkillData> {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "30.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "15_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "3_000")
    private long duration;

    public SilencingArrow(final Assassin module) {
        super(module);
    }

    @Override
    public Class<BowSkillData> getClassOfData() {
        return BowSkillData.class;
    }

    private long getDuration(final int level) {
        return this.duration + (level * 1000L);
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Left-Click with a Bow to Prepare.",
                "",
                "Your next arrow will silence your",
                UtilString.format("target for %s.", this.getValueString(Long.class, this::getDuration, level)),
                "Making them unable to use any active skills.",
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
        this.addUser(new BowSkillData(player, level));

        super.onActivate(player, level);
    }

    @Override
    public void onHitByEntity(final Player damager, final Entity damagee, final CustomPostDamageEvent event, final BowSkillData data) {
        if (damagee instanceof Player) {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit <var> with <green><var></green>.", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel())));
            UtilMessage.simpleMessage(damagee, this.getModule().getName(), "<var> hit you with <green><var></green>.", Arrays.asList(event.getDamagerName(), this.getDisplayName(data.getLevel())));
        } else {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit a <var> with <green><var></green>.", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel())));
        }

        if (damagee instanceof LivingEntity) {
            final LivingEntity damageeLivingEntity = UtilJava.cast(LivingEntity.class, damagee);

            this.getInstance().getManagerByClass(EffectManager.class).getModuleByClass(Silenced.class).addUser(new EffectData(damageeLivingEntity, this.getDuration(data.getLevel())));
        }
    }

    @Override
    public void onUpdater(final Player player, final BowSkillData data) {
        ParticleEffect.VILLAGER_HAPPY.display(new Vector(0.0D, 0.0D, 0.0D), 1, data.getArrow().getLocation().add(0.0D, 0.25D, 0.0D), 500);
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 2;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = (level - 1) / 2;

        return this.recharge - (value * 1000L);
    }
}