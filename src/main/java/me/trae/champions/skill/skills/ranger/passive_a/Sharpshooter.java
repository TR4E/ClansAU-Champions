package me.trae.champions.skill.skills.ranger.passive_a;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Ranger;
import me.trae.champions.skill.skills.ranger.passive_a.data.SharpshooterData;
import me.trae.champions.skill.types.PassiveBowSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.updater.annotations.Update;
import me.trae.core.updater.interfaces.Updater;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilTime;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public class Sharpshooter extends PassiveBowSkill<Ranger, SharpshooterData> implements Updater {

    @ConfigInject(type = Double.class, path = "Damage-Multiplier", defaultValue = "0.50")
    private double damageMultiplier;

    @ConfigInject(type = Long.class, path = "Last-Hit-Expiration", defaultValue = "5_000")
    private long lastHitExpiration;

    public Sharpshooter(final Ranger module) {
        super(module, PassiveSkillType.PASSIVE_A);
    }

    @Override
    public Class<SharpshooterData> getClassOfData() {
        return SharpshooterData.class;
    }

    private int getMaxCharges(final int level) {
        return level;
    }

    private double getDamage(final int level) {
        return level * this.damageMultiplier;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "For each consecutive hit,",
                String.format("<green>%s</green> additional damage per charge", this.getDamage(level)),
                "",
                String.format("You can store a maximum of <green>%s</green> charges.", this.getMaxCharges(level)),
        };
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean resetDataOnShoot() {
        return false;
    }

    @Override
    public void onActivate(final Player player, final int level) {
        this.addUser(new SharpshooterData(player, level));
    }

    @Override
    public void onHitByEntity(final Player damager, final Entity damagee, final CustomPostDamageEvent event, final SharpshooterData data) {
        if (!(damagee instanceof Player)) {
            return;
        }

        if (data.getCharges() >= this.getMaxCharges(data.getLevel())) {
            return;
        }

        data.addCharge();

        data.updateLastHit();

        final double damage = data.getCharges() * this.getDamage(data.getLevel());

        event.setDamage(event.getDamage() + damage);

        event.setReason(this.getDisplayName(data.getLevel()), 1000L);

        final String damageString = String.format("<white>+%s dmg</white>", damage);

        UtilMessage.simpleMessage(damager, this.getName(), "You hit <var> with Charge <green><var></green> (<var>).", Arrays.asList(event.getDamageeName(), String.valueOf(data.getCharges()), damageString));
        UtilMessage.simpleMessage(damagee, this.getName(), "<var> hit you with Charge <green><var></green> (<var>).", Arrays.asList(event.getDamagerName(), String.valueOf(data.getCharges()), damageString));
    }

    @Update(delay = 250L)
    public void onUpdater() {
        this.getUsers().values().removeIf(data -> {
            if (data.getLastHit() == 0L || !(UtilTime.elapsed(data.getLastHit(), this.lastHitExpiration))) {
                return false;
            }

            final Player player = Bukkit.getPlayer(data.getUUID());
            if (player != null) {
                UtilMessage.simpleMessage(player, this.getModule().getName(), "<green><var></green> has ended.", Collections.singletonList(this.getDisplayName(data.getLevel())));
            }

            return true;
        });
    }
}