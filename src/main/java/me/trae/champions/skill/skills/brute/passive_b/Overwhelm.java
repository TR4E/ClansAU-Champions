package me.trae.champions.skill.skills.brute.passive_b;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Brute;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.UtilString;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class Overwhelm extends PassiveSkill<Brute, SkillData> implements Listener {

    @ConfigInject(type = Double.class, path = "Bonus-Damage", defaultValue = "1.0")
    private double bonusDamage;

    @ConfigInject(type = Double.class, path = "Base-Heath", defaultValue = "2.0")
    private double baseHealth;

    public Overwhelm(final Brute module) {
        super(module, PassiveSkillType.PASSIVE_B);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private double getMaxDamage(final int level) {
        return level * 0.5D;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                UtilString.format("You deal %s bonus damage for every", this.getValueString(Double.class, this.bonusDamage)),
                UtilString.format("%s more health you have than your opponent.", this.getValueString(Double.class, this.baseHealth)),
                "",
                UtilString.format("You can deal a maximum of %s bonus damage.", this.getValueString(Double.class, this::getMaxDamage, level))
        };
    }

    @EventHandler
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

        final int level = getLevel(damager);
        if (level == 0) {
            return;
        }

        final LivingEntity damagee = event.getDamageeByClass(LivingEntity.class);

        double damage = (damager.getHealth() - damagee.getHealth()) / 2;

        if (damage <= 0) {
            return;
        }

        damage = Math.min(damage, this.getMaxDamage(level));

        event.setDamage(event.getDamage() + damage);

        event.setLightReason(this.getDisplayName(level), 1000L);
    }
}