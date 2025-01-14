package me.trae.champions.skill.skills.ranger.passive_a;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Ranger;
import me.trae.champions.skill.types.PassiveBowSkill;
import me.trae.champions.skill.types.data.BowSkillData;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Precision extends PassiveBowSkill<Ranger, BowSkillData> implements Listener {

    @ConfigInject(type = Double.class, path = "Damage-Multiplier", defaultValue = "0.5")
    private double damageMultiplier;

    public Precision(final Ranger module) {
        super(module, PassiveSkillType.PASSIVE_A);
    }

    @Override
    public Class<BowSkillData> getClassOfData() {
        return BowSkillData.class;
    }

    private double getDamage(final int level) {
        return level * this.damageMultiplier;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                String.format("Your arrows deal %s bonus damage on hit.", this.getValueString(Double.class, this::getDamage, level))
        };
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void onHitByEntity(final Player damager, final Entity damagee, final CustomPostDamageEvent event, final BowSkillData data) {
        event.setDamage(event.getDamage() + this.getDamage(data.getLevel()));

        event.setReason(this.getDisplayName(data.getLevel()), 1000L);
    }
}