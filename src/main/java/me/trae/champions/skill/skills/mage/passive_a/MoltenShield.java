package me.trae.champions.skill.skills.mage.passive_a;

import me.trae.api.damage.events.damage.CustomPreDamageEvent;
import me.trae.champions.role.types.Mage;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;

public class MoltenShield extends PassiveSkill<Mage, SkillData> implements Listener {

    public MoltenShield(final Mage module) {
        super(module, PassiveSkillType.PASSIVE_A);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "You are immune to lava and fire damage."
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

    @EventHandler
    public void onCustomPreDamage(final CustomPreDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(Arrays.asList(EntityDamageEvent.DamageCause.LAVA, EntityDamageEvent.DamageCause.FIRE, EntityDamageEvent.DamageCause.FIRE_TICK).contains(event.getCause()))) {
            return;
        }

        if (!(event.getDamagee() instanceof Player)) {
            return;
        }

        final Player damagee = event.getDamageeByClass(Player.class);

        final int level = this.getLevel(damagee);
        if (level == 0) {
            return;
        }

        event.setCancelled(true);
    }
}