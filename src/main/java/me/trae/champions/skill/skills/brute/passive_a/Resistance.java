package me.trae.champions.skill.skills.brute.passive_a;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Brute;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class Resistance extends PassiveSkill<Brute, SkillData> implements Listener {

    @ConfigInject(type = Integer.class, path = "Take-Damage-Percentage", defaultValue = "10")
    private int takeDamagePercentage;

    @ConfigInject(type = Integer.class, path = "Deal-Damage-Percentage", defaultValue = "10")
    private int dealDamagePercentage;

    public Resistance(final Brute module) {
        super(module, PassiveSkillType.PASSIVE_A);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private int getTakeDamagePercentage(final int level) {
        return this.takeDamagePercentage * level;
    }

    private int getDealDamagePercentage(final int level) {
        return this.dealDamagePercentage * level;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                String.format("You take <green>%s</green> less damage", this.getTakeDamagePercentage(level) + "%"),
                String.format("but you deal <green>%s</green> less as well.", this.getDealDamagePercentage(level) + "%")
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

    @EventHandler(priority = EventPriority.LOW)
    public void onCustomPostDamage(final CustomPostDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getDamagee() instanceof Player) {
            final Player damagee = event.getDamageeByClass(Player.class);

            final int level = getLevel(damagee);

            if (level > 0) {
                final double modifier = this.getTakeDamagePercentage(level) * 0.01D;

                event.setDamage(event.getDamage() * (1.0 - modifier));
            }
        }

        if (event.getDamager() instanceof Player) {
            final Player damager = event.getDamagerByClass(Player.class);

            final int level = getLevel(damager);

            if (level > 0) {
                final double modifier = this.getDealDamagePercentage(level) * 0.01D;

                event.setDamage(event.getDamage() * (1.0 - modifier));
            }
        }
    }
}