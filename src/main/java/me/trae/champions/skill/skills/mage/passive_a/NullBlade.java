package me.trae.champions.skill.skills.mage.passive_a;

import me.trae.api.damage.events.damage.CustomPreDamageEvent;
import me.trae.champions.role.types.Mage;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.Core;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.energy.EnergyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class NullBlade extends PassiveSkill<Mage, SkillData> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy-Sucked", defaultValue = "2")
    private float energySucked;

    public NullBlade(final Mage module) {
        super(module, PassiveSkillType.PASSIVE_A);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private float getEnergySucked(final int level) {
        final int value = level - 1;

        return this.energySucked + value;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                String.format("Your sword sucks <green>%s</green> energy from", this.getEnergySucked(level)),
                "opponents with every attack."
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

        if (!(event.getDamagee() instanceof Player)) {
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

        final Player damagee = event.getDamageeByClass(Player.class);

        final EnergyManager energyManager = this.getInstanceByClass(Core.class).getManagerByClass(EnergyManager.class);

        final float energySucked = this.getEnergySucked(level);

        energyManager.regenerate(damager, energySucked);
        energyManager.degenerate(damagee, energySucked);
    }
}