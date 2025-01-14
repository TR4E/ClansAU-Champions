package me.trae.champions.skill.skills.knight.passive_a;

import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.api.damage.events.damage.CustomDamageEvent;
import me.trae.api.damage.utility.UtilDamage;
import me.trae.champions.role.types.Knight;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.UtilEntity;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilServer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;
import java.util.Collections;

public class Cleave extends PassiveSkill<Knight, SkillData> implements Listener {

    @ConfigInject(type = Integer.class, path = "Distance", defaultValue = "3")
    private int distance;

    @ConfigInject(type = Double.class, path = "Damage", defaultValue = "3.0")
    private double damage;

    public Cleave(final Knight module) {
        super(module, PassiveSkillType.PASSIVE_A);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private int getDistance(final int level) {
        return this.distance + level;
    }

    private double getDamage(final int level) {
        return this.damage + level;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Your attacks hit all opponents",
                String.format("within %s blocks of your target.", this.getValueString(Integer.class, this::getDistance, level)),
                "",
                "Only applies to Axes."
        };
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

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (!(event.getDamagee() instanceof LivingEntity)) {
            return;
        }

        final Player damager = event.getDamagerByClass(Player.class);

        if (!(SkillType.AXE.isItemStack(damager.getEquipment().getItemInHand()))) {
            return;
        }

        final int level = this.getLevel(damager);
        if (level == 0) {
            return;
        }

        event.setDamage(event.getDamage() + this.getDamage(level));

        event.setLightReason(this.getDisplayName(level), 2000L);

        final LivingEntity damagee = event.getDamageeByClass(LivingEntity.class);

        int count = 0;

        for (final LivingEntity targetEntity : UtilEntity.getNearbyEntities(LivingEntity.class, damagee.getLocation(), this.getDistance(level))) {
            if (targetEntity.equals(damager) || targetEntity.equals(damagee)) {
                continue;
            }

            if (targetEntity instanceof Player) {
                final Player targetPlayer = UtilJava.cast(Player.class, targetEntity);

                final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, damager, targetPlayer);
                UtilServer.callEvent(friendlyFireEvent);
                if (friendlyFireEvent.isCancelled() || !(friendlyFireEvent.isVulnerable())) {
                    continue;
                }

                UtilMessage.simpleMessage(targetPlayer, this.getModule().getName(), "<var> hit you with <green><var></green>.", Arrays.asList(friendlyFireEvent.getPlayerName(), this.getDisplayName(level)));
            }

            UtilDamage.damage(targetEntity, damager, EntityDamageEvent.DamageCause.CUSTOM, this.getDamage(level), this.getDisplayName(level), 1000L);

            count++;
        }

        if (count > 0) {
            UtilMessage.simpleMessage(damager, this.getName(), "You have affected <yellow><var></yellow> nearby enemies.", Collections.singletonList(String.valueOf(count)));
        }
    }
}