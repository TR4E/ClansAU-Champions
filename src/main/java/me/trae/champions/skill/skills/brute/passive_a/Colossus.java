package me.trae.champions.skill.skills.brute.passive_a;

import me.trae.api.damage.events.CustomKnockbackEvent;
import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Brute;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.utility.UtilString;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;

public class Colossus extends PassiveSkill<Brute, SkillData> implements Listener {

    public Colossus(final Brute module) {
        super(module, PassiveSkillType.PASSIVE_A);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private int getKnockback(final int level) {
        return 25 * level;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                UtilString.format("You take %s reduced knockback.", this.getValueString(Integer.class, this::getKnockback, level) + "%")
        };
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @EventHandler
    public void onCustomKnockback(final CustomKnockbackEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final CustomPostDamageEvent damageEvent = event.getDamageEvent();

        if (!(Arrays.asList(EntityDamageEvent.DamageCause.ENTITY_ATTACK, EntityDamageEvent.DamageCause.PROJECTILE).contains(damageEvent.getCause()))) {
            return;
        }

        if (!(damageEvent.getDamagee() instanceof Player)) {
            return;
        }

        final Player damagee = damageEvent.getDamageeByClass(Player.class);

        final int level = this.getLevel(damagee);

        if (level == 0) {
            return;
        }

        event.setKnockback(event.getKnockback() * (1.0D - (this.getKnockback(level) * 0.01D)));
    }
}