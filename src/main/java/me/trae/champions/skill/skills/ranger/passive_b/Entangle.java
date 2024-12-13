package me.trae.champions.skill.skills.ranger.passive_b;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Ranger;
import me.trae.champions.skill.types.PassiveBowSkill;
import me.trae.champions.skill.types.data.BowSkillData;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.utility.UtilEntity;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.UtilTime;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Entangle extends PassiveBowSkill<Ranger, BowSkillData> {

    public Entangle(final Ranger module) {
        super(module, PassiveSkillType.PASSIVE_B);
    }

    @Override
    public Class<BowSkillData> getClassOfData() {
        return BowSkillData.class;
    }

    private int getAmplifier(final int level) {
        return level;
    }

    private long getDuration(final int level) {
        return level * 1000L;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                String.format("Your arrows apply Slowness <green>%s</green>", this.getAmplifier(level)),
                String.format("to your opponent for <green>%s</green>.", UtilTime.getTime(this.getDuration(level)))
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
    public void onHitByEntity(final Player damager, final Entity damagee, final CustomPostDamageEvent event, final BowSkillData data) {
        if (!(damagee instanceof LivingEntity)) {
            return;
        }

        final LivingEntity damageeLivingEntity = UtilJava.cast(LivingEntity.class, damagee);

        UtilEntity.givePotionEffect(damageeLivingEntity, PotionEffectType.SLOW, this.getAmplifier(data.getLevel()), this.getDuration(data.getLevel()));
    }
}