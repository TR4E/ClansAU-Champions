package me.trae.champions.skill.skills.knight.passive_b;

import me.trae.api.damage.events.damage.CustomDamageEvent;
import me.trae.champions.role.types.Knight;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.skills.knight.passive_b.data.SwordsmanshipData;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.Core;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.gamer.Gamer;
import me.trae.core.gamer.GamerManager;
import me.trae.core.updater.annotations.Update;
import me.trae.core.updater.interfaces.Updater;
import me.trae.core.utility.UtilMath;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.UtilTime;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;

public class Swordsmanship extends PassiveSkill<Knight, SwordsmanshipData> implements Listener, Updater {

    private final GamerManager GAMER_MANAGER;

    @ConfigInject(type = Long.class, path = "Charge-Duration", defaultValue = "3_000")
    private long chargeDuration;

    @ConfigInject(type = Long.class, path = "Last-Damaged-Duration", defaultValue = "2_000")
    private long lastDamagedDuration;

    @ConfigInject(type = Integer.class, path = "Max-Charges", defaultValue = "2")
    private int maxCharges;

    @ConfigInject(type = Double.class, path = "Damage-Multiplier", defaultValue = "1.0")
    private double damageMultiplier;

    public Swordsmanship(final Knight module) {
        super(module, PassiveSkillType.PASSIVE_B);

        this.GAMER_MANAGER = this.getInstance(Core.class).getManagerByClass(GamerManager.class);
    }

    @Override
    public Class<SwordsmanshipData> getClassOfData() {
        return SwordsmanshipData.class;
    }

    @Override
    public String[] getDescription(final int level) {
        final int maxCharges = Math.min(level, this.maxCharges);

        return new String[]{
                "Prepare a powerful sword attack,",
                "you gain 1 charge every 3 seconds.",
                "",
                String.format("You can store a maximum of <green>%s</green> charges.", maxCharges),
                "",
                "When you attack, your damage is",
                "increased by the number of your charges",
                "and your charges reset to 0.",
                "",
                "This only applies to Swords."
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
    public boolean canActivate(final Player player) {
        return SkillType.SWORD.isItemStack(player.getEquipment().getItemInHand());
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

        final LivingEntity damagee = event.getDamageeByClass(LivingEntity.class);
        final Player damager = event.getDamagerByClass(Player.class);

        final SwordsmanshipData data = this.getUserByPlayer(damager);
        if (data == null) {
            return;
        }

        if (!(data.hasCharges())) {
            return;
        }

        if (!(this.canActivate(damager))) {
            return;
        }

        final double damage = data.getCharges() * this.damageMultiplier;

        event.setDamage(event.getDamage() + damage);

        event.setReason(this.getDisplayName(data.getLevel()), 1000L);

        new SoundCreator(Sound.ZOMBIE_METAL, 1.0F, 1.5F).play(damagee.getLocation());

        final String damageString = String.format("<white>+%s dmg", damage);

        if (damagee instanceof Player) {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit <var> with <green><var></green> (<var><reset>).", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel()), damageString));
            UtilMessage.simpleMessage(damagee, this.getModule().getName(), "<var> hit you with <green><var></green> (<var><reset>).", Arrays.asList(event.getDamagerName(), this.getDisplayName(data.getLevel()), damageString));
        } else {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit a <var> with <green><var></green> (<var><reset>).", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel()), damageString));
        }

        this.removeUser(damager);
    }

    @Update(delay = 250L)
    public void onUpdater() {
        for (final Player player : this.getModule().getUsers()) {
            if (!(this.canActivate(player)) && this.isUserByPlayer(player)) {
                this.removeUser(player);
                continue;
            }

            final int level = getLevel(player);
            if (level == 0) {
                continue;
            }

            final Gamer gamer = this.GAMER_MANAGER.getGamerByPlayer(player);
            if (gamer == null) {
                continue;
            }

            if (!(gamer.hasLastDamaged(this.lastDamagedDuration))) {
                continue;
            }

            if (!(this.isUserByPlayer(player))) {
                this.addUser(new SwordsmanshipData(player, level));
            }

            final SwordsmanshipData data = this.getUserByPlayer(player);

            if (data.getCharges() >= this.maxCharges) {
                continue;
            }

            if (!(UtilTime.elapsed(data.getLastUpdated(), this.chargeDuration))) {
                continue;
            }

            data.updateLastUpdated();

            data.setCharges(UtilMath.getMinAndMax(Integer.class, 0, Math.min(level, this.maxCharges), data.getCharges() + 1));

            UtilMessage.simpleMessage(player, this.getModule().getName(), UtilString.pair(String.format("%s Charges", this.getName()), String.format("<yellow>%s", data.getCharges())));
        }
    }
}