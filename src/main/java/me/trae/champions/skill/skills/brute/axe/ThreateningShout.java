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

import java.util.Arrays;
import java.util.Collections;

public class ThreateningShout extends ActiveSkill<Brute, SkillData> {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "40.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "19_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "2_000")
    private long duration;

    @ConfigInject(type = Integer.class, path = "Amplifier", defaultValue = "1")
    private int amplifier;

    @ConfigInject(type = Integer.class, path = "Distance", defaultValue = "4")
    private int distance;

    public ThreateningShout(final Brute module) {
        super(module, ActiveSkillType.AXE);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private int getDistance(final int level) {
        return this.distance + level;
    }

    private long getDuration(final int level) {
        return this.duration + (level * 1000L);
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with an Axe to Activate.",
                "",
                "Release a roar, which frightens all enemies",
                String.format("within <green>%s</green> blocks", this.getDistance(level)),
                String.format("and grants them Weakness %s for <green>%s</green>", this.amplifier, UtilTime.getTime(this.getDuration(level))),
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
        new SoundCreator(Sound.ENDERDRAGON_GROWL, 2.0F, 2.0F).play(player.getLocation());

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));

        this.handleEffect(player, level);
    }

    @Override
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            UtilEntity.removePotionEffect(player, PotionEffectType.WEAKNESS);
        }

        super.reset(player);
    }

    private void handleEffect(final Player player, final int level) {
        int count = 0;

        for (final Player targetPlayer : UtilEntity.getNearbyEntities(Player.class, player.getLocation(), this.getDistance(level))) {
            if (targetPlayer == player) {
                continue;
            }

            final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, player, targetPlayer);
            UtilServer.callEvent(friendlyFireEvent);
            if (friendlyFireEvent.isCancelled() || !(friendlyFireEvent.isVulnerable())) {
                continue;
            }

            UtilEntity.givePotionEffect(targetPlayer, PotionEffectType.WEAKNESS, this.amplifier, this.getDuration(level));

            UtilMessage.simpleMessage(targetPlayer, this.getModule().getName(), "<var> used <green><var></green>.", Arrays.asList(friendlyFireEvent.getPlayerName(), this.getDisplayName(level)));

            count++;
        }

        if (count > 0) {
            UtilMessage.simpleMessage(player, this.getName(), "You have affected <yellow><var></yellow> nearby enemies.", Collections.singletonList(String.valueOf(count)));
        }
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 2;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = (level - 1) * 2;

        return this.recharge - (value * 1000L);
    }
}