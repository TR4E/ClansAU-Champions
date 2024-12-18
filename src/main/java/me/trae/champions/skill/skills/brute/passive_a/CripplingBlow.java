package me.trae.champions.skill.skills.brute.passive_a;

import me.trae.api.damage.events.damage.CustomDamageEvent;
import me.trae.champions.role.types.Brute;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.UtilEntity;
import me.trae.core.utility.UtilTime;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

public class CripplingBlow extends PassiveSkill<Brute, SkillData> implements Listener {

    @ConfigInject(type = Integer.class, path = "Slowness-Amplifier", defaultValue = "1")
    private int slownessAmplifier;

    public CripplingBlow(final Brute module) {
        super(module, PassiveSkillType.PASSIVE_A);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private int getSlownessAmplifier(final int level) {
        return this.slownessAmplifier;
    }

    private long getSlownessDuration(final int level) {
        return (1 + (level / 2)) * 1000L;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Your powerful axe blows give",
                String.format("your opponents Slow %s for <green>%s</green>.", this.getSlownessAmplifier(level), UtilTime.getTime(this.getSlownessDuration(level))),
                "as well as no knockback"
        };
    }

    @EventHandler
    public void onCustomDamage(final CustomDamageEvent event) {
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

        if (!(SkillType.AXE.isItemStack(damager.getEquipment().getItemInHand()))) {
            return;
        }

        final LivingEntity damagee = event.getDamageeByClass(LivingEntity.class);

        UtilEntity.givePotionEffect(damagee, PotionEffectType.SLOW, this.getSlownessAmplifier(level), this.getSlownessDuration(level));

        event.setKnockback(0.0D);

        event.setLightReason(this.getDisplayName(level), 1000L);
    }
}