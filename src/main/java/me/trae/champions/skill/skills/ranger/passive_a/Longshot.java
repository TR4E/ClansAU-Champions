package me.trae.champions.skill.skills.ranger.passive_a;

import me.trae.api.damage.data.DamageReason;
import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Ranger;
import me.trae.champions.skill.types.PassiveBowSkill;
import me.trae.champions.skill.types.data.BowSkillData;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.UtilMath;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.particle.ParticleEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Longshot extends PassiveBowSkill<Ranger, BowSkillData> {

    @ConfigInject(type = Double.class, path = "Max-Damage", defaultValue = "14.0")
    private double maxDamage;

    public Longshot(final Ranger module) {
        super(module, PassiveSkillType.PASSIVE_A);
    }

    @Override
    public Class<BowSkillData> getClassOfData() {
        return BowSkillData.class;
    }

    private double getMaxDamage(final int level) {
        return this.maxDamage + (level * 2);
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Shoot an arrow that gains additional",
                "damage the further the target hit is.",
                "",
                UtilString.format("Caps out at %s damage.", this.getValueString(Double.class, this::getMaxDamage, level)),
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
    public boolean requireFullBowPullRange() {
        return true;
    }

    @Override
    public void onHitByEntity(final Player damager, final Entity damagee, final CustomPostDamageEvent event, final BowSkillData data) {
        final double length = UtilMath.offset(data.getLocation().toVector(), damagee.getLocation().toVector());

        double damage = event.getDamage();

        damage += length / 3.0D - 4.0D;

        damage = UtilMath.getMinAndMax(Double.class, 0.0D, this.getMaxDamage(data.getLevel()), damage);

        event.setDamage(damage);

        event.setReason(new DamageReason(this.getDisplayName(data.getLevel()), 2_000L) {
            @Override
            public String getExtraName() {
                return UtilString.format(" <gray>from <green>%s</green> blocks", Math.round(length));
            }
        });
    }

    @Override
    public void onUpdater(final Player player, final BowSkillData data) {
        ParticleEffect.FIREWORKS_SPARK.display(new Vector(0.0D, 0.0D, 0.0D), 1, data.getArrow().getLocation().add(0.0D, 0.25D, 0.0D), 500);
    }
}