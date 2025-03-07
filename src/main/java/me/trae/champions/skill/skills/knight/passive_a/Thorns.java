package me.trae.champions.skill.skills.knight.passive_a;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.api.damage.utility.UtilDamage;
import me.trae.champions.role.types.Knight;
import me.trae.champions.skill.skills.knight.passive_a.data.ThornsData;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.UtilTime;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class Thorns extends PassiveSkill<Knight, ThornsData> implements Listener {

    public Thorns(final Knight module) {
        super(module, PassiveSkillType.PASSIVE_A);
    }

    @Override
    public Class<ThornsData> getClassOfData() {
        return ThornsData.class;
    }

    private double getDamage(final int level) {
        return level;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                UtilString.format("Enemies take %s damage", this.getValueString(Double.class, this::getDamage, level)),
                "when they hit you using a melee attack."
        };
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCustomPostDamage(final CustomPostDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        if (!(event.getDamagee() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof LivingEntity)) {
            return;
        }

        final Player damagee = event.getDamageeByClass(Player.class);

        final int level = getLevel(damagee);
        if (level == 0) {
            return;
        }

        if (!(this.isUserByPlayer(damagee))) {
            this.addUser(new ThornsData(damagee, level));
        }

        final ThornsData data = this.getUserByPlayer(damagee);

        final LivingEntity damager = event.getDamagerByClass(LivingEntity.class);

        if (data.isHitByEntity(damager) && !(UtilTime.elapsed(data.getHitByEntity(damager), 2_000))) {
            return;
        }

        UtilDamage.damage(damager, damagee, EntityDamageEvent.DamageCause.CUSTOM, this.getDamage(data.getLevel()), this.getDisplayName(data.getLevel()), 1000L);

        data.addHit(damager);
    }
}