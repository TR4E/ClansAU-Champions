package me.trae.champions.skill.skills.knight.passive_a;

import me.trae.api.damage.events.damage.CustomDamageEvent;
import me.trae.champions.role.types.Knight;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.utility.UtilString;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class Fury extends PassiveSkill<Knight, SkillData> implements Listener {

    public Fury(final Knight module) {
        super(module, PassiveSkillType.PASSIVE_A);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private double getDamage(final int level) {
        return level * 0.5D;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                UtilString.format("Your attacks deal a bonus %s damage", this.getValueString(Double.class, this::getDamage, level))
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
    public void onCustomDamage(final CustomDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (!(event.getDamagee() instanceof LivingEntity)) {
            return;
        }

        final Player damager = event.getDamagerByClass(Player.class);

        final int level = this.getLevel(damager);
        if (level == 0) {
            return;
        }

        event.setDamage(event.getDamage() + this.getDamage(level));

        event.setLightReason(this.getDisplayName(level), 2000L);
    }
}