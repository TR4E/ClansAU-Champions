package me.trae.champions.skill.skills.knight.passive_b;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Knight;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class Sacrifice extends PassiveSkill<Knight, SkillData> implements Listener {

    @ConfigInject(type = Integer.class, path = "Base-Percentage", defaultValue = "10")
    private int basePercentage;

    public Sacrifice(final Knight module) {
        super(module, PassiveSkillType.PASSIVE_B);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private int getPercentage(final int level) {
        return this.basePercentage * level;
    }

    @Override
    public String[] getDescription(final int level) {
        final String percentageString = this.getValueString(Integer.class, this::getPercentage, level);

        return new String[]{
                String.format("Deal an extra %s melee damage,", percentageString + "%"),
                String.format("but you now also take %s extra damage from melee attacks.", percentageString + "%")
        };
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCustomPostDamage(final CustomPostDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        if (event.getDamagee() instanceof Player) {
            final Player damagee = event.getDamageeByClass(Player.class);

            final int level = this.getLevel(damagee);

            if (level > 0) {
                final double modifier = this.getPercentage(level) * 0.01D;

                event.setDamage(event.getDamage() * (1.0D + modifier));
            }
        }

        if (event.getDamager() instanceof Player) {
            final Player damager = event.getDamagerByClass(Player.class);

            final int level = this.getLevel(damager);

            if (level > 0) {
                final double modifier = this.getPercentage(level) * 0.01D;

                event.setDamage(event.getDamage() * (1.0D + modifier));
            }
        }
    }
}