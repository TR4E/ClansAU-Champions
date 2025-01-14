package me.trae.champions.skill.skills.knight.axe;

import me.trae.api.damage.events.CustomKnockbackEvent;
import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Knight;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.UtilEntity;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class HoldPosition extends ActiveSkill<Knight, SkillData> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "60.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "25_000")
    private long recharge;

    @ConfigInject(type = Integer.class, path = "Resistance-Amplifier", defaultValue = "2")
    private int resistanceAmplifier;

    @ConfigInject(type = Integer.class, path = "Slowness-Amplifier", defaultValue = "3")
    private int slownessAmplifier;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "5_000")
    private long duration;

    public HoldPosition(final Knight module) {
        super(module, ActiveSkillType.AXE);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private long getDuration(final int level) {
        return this.duration + ((level / 2) * 1000L);
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Hold your position, gaining",
                String.format("Resistance %s, Slowness %s, and no", this.getValueString(Integer.class, this.resistanceAmplifier), this.getValueString(Integer.class, this.slownessAmplifier)),
                String.format("knockback for %s", this.getValueString(Long.class, this::getDuration, level)),
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyString(level)))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        new SoundCreator(Sound.ENDERDRAGON_GROWL, 1.0F, 0.5F).play(player.getLocation());

        final long duration = this.getDuration(level);

        this.addUser(new SkillData(player, level, duration));

        UtilEntity.givePotionEffect(player, PotionEffectType.DAMAGE_RESISTANCE, this.resistanceAmplifier, duration);
        UtilEntity.givePotionEffect(player, PotionEffectType.SLOW, this.slownessAmplifier, duration);

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @Override
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            UtilEntity.removePotionEffect(player, PotionEffectType.DAMAGE_RESISTANCE);
            UtilEntity.removePotionEffect(player, PotionEffectType.SLOW);
        }

        super.reset(player);
    }

    @Override
    public void onExpire(final Player player, final SkillData data) {
        new SoundCreator(Sound.NOTE_STICKS).play(player);

        UtilMessage.simpleMessage(player, this.getModule().getName(), "<green><var></green> has ended.", Collections.singletonList(this.getDisplayName(data.getLevel())));
    }

    @EventHandler
    public void onCustomKnockback(final CustomKnockbackEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final CustomPostDamageEvent damageEvent = event.getDamageEvent();

        if (!(damageEvent.getDamagee() instanceof Player)) {
            return;
        }

        final Player damagee = damageEvent.getDamageeByClass(Player.class);

        if (!(this.isUserByPlayer(damagee))) {
            return;
        }

        event.setCancelled(true);
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 5;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = (level - 1) * 2;

        return this.recharge - (value * 1000L);
    }
}