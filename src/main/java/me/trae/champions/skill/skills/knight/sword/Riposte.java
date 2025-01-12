package me.trae.champions.skill.skills.knight.sword;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.api.damage.events.damage.CustomPreDamageEvent;
import me.trae.champions.role.types.Knight;
import me.trae.champions.skill.skills.knight.sword.data.RiposteData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.updater.interfaces.Updater;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.UtilTime;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;
import java.util.Collections;

public class Riposte extends ActiveSkill<Knight, RiposteData> implements Listener, Updater {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "60.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "20_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Prepare-Duration", defaultValue = "1_000")
    private long prepareDuration;

    @ConfigInject(type = Long.class, path = "Invulnerable-Duration", defaultValue = "1_000")
    private long invulnerableDuration;

    public Riposte(final Knight module) {
        super(module, ActiveSkillType.SWORD);
    }

    @Override
    public Class<RiposteData> getClassOfData() {
        return RiposteData.class;
    }

    private long getInvulnerableDuration(final int level) {
        return this.invulnerableDuration + ((level * 1000L) / 2);
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with a Sword to Activate.",
                "",
                "Become invulnerable to all",
                String.format("melee damage for <green>%s</green> when countered", UtilTime.getTime(this.getInvulnerableDuration(level))),
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyString(level)))
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

    @Override
    public void onActivate(final Player player, final int level) {
        new SoundCreator(Sound.IRONGOLEM_HIT, 2.0F, 1.0F).play(player.getLocation());

        this.addUser(new RiposteData(player, level, this.prepareDuration));

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You have prepared <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @Override
    public void onExpire(final Player player, final RiposteData data) {
        if (data.isInvulnerable()) {
            UtilMessage.message(player, this.getName(), "You are no longer Invulnerable.");
            return;
        }

        new SoundCreator(Sound.NOTE_STICKS).play(player);

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You failed <green><var></green>.", Collections.singletonList(this.getDisplayName(data.getLevel())));
    }

    @EventHandler(priority = EventPriority.LOW)
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
        final LivingEntity damager = event.getDamagerByClass(LivingEntity.class);

        final RiposteData data = this.getUserByPlayer(damagee);
        if (data == null) {
            return;
        }

        if (data.isInvulnerable()) {
            return;
        }

        event.setCancelled(true);

        new SoundCreator(Sound.ZOMBIE_METAL).play(damagee.getLocation());

        data.setDuration(this.getInvulnerableDuration(data.getLevel()));

        data.setInvulnerable(true);

        UtilMessage.simpleMessage(damagee, this.getModule().getName(), "You used <green><var></green> against <var>.", Arrays.asList(this.getDisplayName(data.getLevel()), event.getDamagerName()));

        UtilMessage.simpleMessage(damager, this.getName(), "Countered an attack from <var>.", Collections.singletonList(event.getDamageeName()));
    }

    @EventHandler(priority = EventPriority.HIGH)
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

        if (!(event.getDamager() instanceof LivingEntity)) {
            return;
        }

        final Player damagee = event.getDamageeByClass(Player.class);
        final LivingEntity damager = event.getDamagerByClass(LivingEntity.class);

        final RiposteData data = this.getUserByPlayer(damagee);
        if (data == null) {
            return;
        }

        if (!(data.isInvulnerable())) {
            return;
        }

        event.setCancelled(true);

        new SoundCreator(Sound.ZOMBIE_METAL).play(damagee.getLocation());

        UtilMessage.simpleMessage(damager, this.getName(), "<var> is invulnerable to melee attacks for <green><var></green>.", Arrays.asList(event.getDamageeName(), data.getRemainingString()));
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 3;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = (int) ((level - 1) * 1.5);

        return this.recharge - (value * 1000L);
    }
}