package me.trae.champions.skill.skills.mage.passive_a;

import me.trae.api.damage.events.damage.CustomPreDamageEvent;
import me.trae.champions.role.types.Mage;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class MagmaBlade extends PassiveSkill<Mage, SkillData> implements Listener {

    public MagmaBlade(final Mage module) {
        super(module, PassiveSkillType.PASSIVE_A);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private double getDamage(final int level) {
        return level;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Your sword scorches opponents,",
                String.format("dealing an additional <green>%s</green> damage", this.getDamage(level)),
                "to those who are on fire."
        };
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @EventHandler
    public void onCustomPreDamage(final CustomPreDamageEvent event) {
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

        if (!(SkillType.SWORD.isItemStack(damager.getEquipment().getItemInHand()))) {
            return;
        }

        final int level = this.getLevel(damager);
        if (level == 0) {
            return;
        }

        final LivingEntity damagee = event.getDamageeByClass(LivingEntity.class);

        if (damagee.getFireTicks() <= 0) {
            return;
        }

        event.setDamage(event.getDamage() + this.getDamage(level));
    }
}