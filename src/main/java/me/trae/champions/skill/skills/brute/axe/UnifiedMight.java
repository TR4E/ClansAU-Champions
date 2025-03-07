package me.trae.champions.skill.skills.brute.axe;

import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Brute;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.UtilEntity;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilServer;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;

public class UnifiedMight extends ActiveSkill<Brute, SkillData> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "30.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "20_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "2_000")
    private long duration;

    @ConfigInject(type = Integer.class, path = "Amplifier", defaultValue = "1")
    private int amplifier;

    @ConfigInject(type = Integer.class, path = "Distance", defaultValue = "6")
    private int distance;

    @ConfigInject(type = Boolean.class, path = "Buff-Self", defaultValue = "true")
    private boolean buffSelf;

    public UnifiedMight(final Brute module) {
        super(module, ActiveSkillType.AXE);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private int getDistance(final int level) {
        return this.distance + level;
    }

    private int getAmplifier(final int level) {
        return this.amplifier;
    }

    private long getDuration(final int level) {
        return this.duration + (level * 1000L);
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with an Axe to Activate.",
                "",
                UtilString.format("Grant all allies within %s blocks", this.getValueString(Integer.class, this::getDistance, level)),
                UtilString.format("Strength %s for %s.", this.getValueString(Integer.class, this::getAmplifier, level), this.getValueString(Long.class, this::getDuration, level)),
                this.buffSelf ? "" : "\nThis does not give you the buff.\n",
                UtilString.pair("<gray>Recharge", UtilString.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", UtilString.format("<green>%s", this.getEnergyString(level)))
        };
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void onActivate(final Player player, final int level) {
        new SoundCreator(Sound.WITHER_SPAWN, 1.0F, 2.0F).play(player.getLocation());

        final long duration = this.getDuration(level);

        this.addUser(new SkillData(player, level, duration));

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));

        for (final Player targetPlayer : UtilEntity.getNearbyEntities(Player.class, player.getLocation(), this.getDistance(level))) {
            final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, player, targetPlayer);
            UtilServer.callEvent(friendlyFireEvent);
            if (friendlyFireEvent.isCancelled() || friendlyFireEvent.isVulnerable()) {
                continue;
            }

            if (!(this.buffSelf)) {
                if (targetPlayer == player) {
                    continue;
                }
            }

            UtilEntity.givePotionEffect(targetPlayer, PotionEffectType.INCREASE_DAMAGE, this.getAmplifier(level), duration);

            if (targetPlayer != player) {
                UtilMessage.simpleMessage(targetPlayer, this.getModule().getName(), "<var> used <green><var></green>.", Arrays.asList(friendlyFireEvent.getPlayerName(), this.getDisplayName(level)));
            }
        }
    }

    @Override
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            if (this.buffSelf) {
                UtilEntity.removePotionEffect(player, PotionEffectType.INCREASE_DAMAGE);
            }
        }

        super.reset(player);
    }

    @Override
    public void onExpire(final Player player, final SkillData data) {
        UtilMessage.simpleMessage(player, this.getModule().getName(), "<green><var></green> has ended.", Collections.singletonList(this.getDisplayName(data.getLevel())));
    }

    @EventHandler
    public void onCustomPostDamage(final CustomPostDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        final Player damager = event.getDamagerByClass(Player.class);

        final SkillData data = this.getUserByPlayer(damager);
        if (data == null) {
            return;
        }

        new SoundCreator(Sound.WOLF_HOWL, 2.0F, 2.0F).play(damager.getLocation());

        event.setReason(this.getDisplayName(data.getLevel()), this.getDuration(data.getLevel()));
    }

    @Override
    public float getEnergy(final int level) {
        final int value = ((level - 1) * 2);

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = (level - 1);

        return this.recharge - (value * 1000L);
    }
}