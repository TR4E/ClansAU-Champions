package me.trae.champions.skill.skills.assassin.passive_a;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.effect.EffectManager;
import me.trae.champions.effect.types.Shock;
import me.trae.champions.role.types.Assassin;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.effect.data.EffectData;
import me.trae.core.utility.UtilEntity;
import me.trae.core.utility.UtilString;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

public class ShockingStrikes extends PassiveSkill<Assassin, SkillData> implements Listener {

    @ConfigInject(type = Integer.class, path = "Amplifier", defaultValue = "1")
    private int amplifier;

    public ShockingStrikes(final Assassin module) {
        super(module, PassiveSkillType.PASSIVE_A);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private int getAmplifier(final int level) {
        return this.amplifier;
    }

    private long getDuration(final int level) {
        return level * 1000L;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                UtilString.format("Your attacks shock targets for %s,", this.getValueString(Long.class, this::getDuration, level)),
                UtilString.format("giving them Slowness %s and Screen-Shake.", this.getValueString(Integer.class, this::getAmplifier, level))
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

        final LivingEntity damagee = event.getDamageeByClass(LivingEntity.class);

        final long duration = this.getDuration(level);

        this.getInstance().getManagerByClass(EffectManager.class).getModuleByClass(Shock.class).addUser(new EffectData(damagee, duration));

        UtilEntity.givePotionEffect(damagee, PotionEffectType.SLOW, this.getAmplifier(level), duration);

        event.setLightReason(this.getDisplayName(level), duration);
    }
}