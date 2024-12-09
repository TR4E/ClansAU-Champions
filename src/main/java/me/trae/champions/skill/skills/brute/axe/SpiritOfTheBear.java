package me.trae.champions.skill.skills.brute.axe;

import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.champions.role.types.Brute;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.*;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class SpiritOfTheBear extends ActiveSkill<Brute, SkillData> {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "50.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "20_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "5_000")
    private long duration;

    @ConfigInject(type = Integer.class, path = "Amplifier", defaultValue = "2")
    private int amplifier;

    @ConfigInject(type = Integer.class, path = "Distance", defaultValue = "5")
    private int distance;

    public SpiritOfTheBear(final Brute module) {
        super(module, ActiveSkillType.AXE);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private int getDistance(final int level) {
        return this.distance + level;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with an Axe to Activate.",
                "",
                "Call upon the Spirit of the Bear",
                String.format("granting all allies within <green>%s</green> blocks", this.getDistance(level)),
                String.format("Resistance %s for %s.", this.amplifier, UtilTime.getTime(this.duration)),
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
        new SoundCreator(Sound.ENDERDRAGON_GROWL, 1.8F, 2.5F).play(player.getLocation());

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));

        this.handleEffect(player, level);
    }

    @Override
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            UtilEntity.removePotionEffect(player, PotionEffectType.DAMAGE_RESISTANCE);
        }

        super.reset(player);
    }

    private void handleEffect(final Player player, final int level) {
        for (final Player targetPlayer : UtilEntity.getNearbyEntities(Player.class, player.getLocation(), this.getDistance(level))) {
            final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, player, targetPlayer);
            UtilServer.callEvent(friendlyFireEvent);
            if (friendlyFireEvent.isCancelled() || friendlyFireEvent.isVulnerable()) {
                continue;
            }

            UtilEntity.givePotionEffect(targetPlayer, PotionEffectType.DAMAGE_RESISTANCE, this.amplifier, this.duration);

            UtilMessage.simpleMessage(targetPlayer, this.getModule().getName(), "You received the Spirit of the Bear!");
        }
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 3;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = (level - 1) * 2;

        return this.recharge - (value * 1000L);
    }
}