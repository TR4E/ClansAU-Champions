package me.trae.champions.skill.skills.assassin.passive_a;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Assassin;
import me.trae.champions.skill.skills.assassin.passive_a.data.RepeatedStrikesData;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.UtilTime;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class RepeatedStrikes extends PassiveSkill<Assassin, RepeatedStrikesData> implements Listener {

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "2_000")
    private long duration;

    @ConfigInject(type = Double.class, path = "Damage-Multiplier", defaultValue = "0.90")
    private double damageMultiplier;

    public RepeatedStrikes(final Assassin module) {
        super(module, PassiveSkillType.PASSIVE_A);
    }

    @Override
    public Class<RepeatedStrikesData> getClassOfData() {
        return RepeatedStrikesData.class;
    }

    private double getMaxDamage(final int level) {
        return level;
    }

    private long getDuration(final int level) {
        return this.duration;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Each time you attack, your damage",
                "increases by 1.",
                "",
                String.format("You can get up to <green>%s</green> bonus damage.", this.getMaxDamage(level)),
                "",
                String.format("Not attacking for %s clears", UtilTime.getTime(this.getDuration(level))),
                "your bonus damage"
        };
    }

    @EventHandler(priority = EventPriority.HIGH)
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

        final int level = this.getLevel(damager);
        if (level == 0) {
            return;
        }

        if (!(this.isUserByPlayer(damager))) {
            this.addUser(new RepeatedStrikesData(damager, level));
        }

        final RepeatedStrikesData data = this.getUserByPlayer(damager);

        event.setDamage((event.getDamage() + data.getAmplifier()) * this.damageMultiplier);

        event.setLightReason(this.getDisplayName(data.getLevel()), 1000L);

        data.setAmplifier(Math.min(data.getLevel(), data.getAmplifier() + 1));

        data.setDuration(this.getDuration(level));
    }
}