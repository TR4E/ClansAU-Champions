package me.trae.champions.skill.skills.ranger.axe;

import me.trae.api.champions.skill.events.SkillActivateEvent;
import me.trae.api.damage.events.damage.CustomPreDamageEvent;
import me.trae.champions.role.types.Ranger;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.champions.skill.types.interfaces.IActiveSkill;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.UtilEntity;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.objects.SoundCreator;
import me.trae.core.weapon.events.WeaponActivateEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;

public class Agility extends ActiveSkill<Ranger, SkillData> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "40.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "30_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "3_000")
    private long duration;

    public Agility(final Ranger module) {
        super(module, ActiveSkillType.AXE);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private int getAmplifier(final int level) {
        return level;
    }

    private long getDuration(final int level) {
        return this.duration + ((level - 1) * 1000L);
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with an Axe to Activate.",
                "",
                "Sprint with great agility, gaining",
                String.format("Speed %s for %s.", this.getValueString(Integer.class, this::getAmplifier, level), this.getValueString(Long.class, this::getDuration, level)),
                "You are immune to melee attacks while sprinting.",
                "Agility ends if you interact.",
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyString(level)))
        };
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void onActivate(final Player player, final int level) {
        new SoundCreator(Sound.NOTE_PLING, 0.5F, 0.5F).play(player.getLocation());

        final long duration = this.getDuration(level);

        UtilEntity.givePotionEffect(player, PotionEffectType.SPEED, this.getAmplifier(level), duration);

        this.addUser(new SkillData(player, level, duration));

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @Override
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            UtilEntity.removePotionEffect(player, PotionEffectType.SPEED);

            UtilMessage.simpleMessage(player, this.getModule().getName(), "<green><var></green> has ended.", Collections.singletonList(this.getDisplayName(this.getUserByPlayer(player).getLevel())));
        }

        super.reset(player);
    }

    @Override
    public void onExpire(final Player player, final SkillData data) {
        new SoundCreator(Sound.NOTE_STICKS).play(player);
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

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        final Player damagee = event.getDamageeByClass(Player.class);
        final Player damager = event.getDamagerByClass(Player.class);

        if (!(damagee.isSprinting())) {
            return;
        }

        final SkillData data = this.getUserByPlayer(damagee);
        if (data == null) {
            return;
        }

        event.setCancelled(true);

        new SoundCreator(Sound.BLAZE_BREATH, 0.5F, 2.0F).play(damagee.getLocation());

        UtilMessage.simpleMessage(damager, this.getModule().getName(), "<var> is using <green><var></green>.", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel())));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSkillActivate(final SkillActivateEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getSkill() instanceof IActiveSkill)) {
            return;
        }

        if (event.getSkill() instanceof Agility) {
            return;
        }

        final Player player = event.getPlayer();

        if (!(this.isUserByPlayer(player))) {
            return;
        }

        this.reset(player);
        this.removeUser(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWeaponActivate(final WeaponActivateEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        if (!(this.isUserByPlayer(player))) {
            return;
        }

        this.reset(player);
        this.removeUser(player);
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 2;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = level - 1;

        return this.recharge - (value * 1000L);
    }
}