package me.trae.champions.skill.skills.mage.sword;

import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.champions.role.types.Mage;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.*;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collections;

public class Cyclone extends ActiveSkill<Mage, SkillData> {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "40.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "12_000")
    private long recharge;

    @ConfigInject(type = Integer.class, path = "Distance", defaultValue = "7")
    private int distance;

    @ConfigInject(type = Double.class, path = "Player-Vector-Y", defaultValue = "2.0")
    private double playerVectorY;

    @ConfigInject(type = Double.class, path = "Target-Vector-Multiplier", defaultValue = "-1.0")
    private double targetVectorMultiplier;

    @ConfigInject(type = Double.class, path = "Strength", defaultValue = "0.5")
    private double strength;

    @ConfigInject(type = Double.class, path = "yBase", defaultValue = "0.0")
    private double yBase;

    @ConfigInject(type = Double.class, path = "yAdd", defaultValue = "0.7")
    private double yAdd;

    @ConfigInject(type = Double.class, path = "yMax", defaultValue = "7.0")
    private double yMax;

    @ConfigInject(type = Boolean.class, path = "groundBoost", defaultValue = "true")
    private boolean groundBoost;

    public Cyclone(final Mage module) {
        super(module, ActiveSkillType.SWORD);
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
                "Right-Click with a Sword to Activate.",
                "",
                "Pulls all enemies within",
                String.format("%s blocks, towards you", this.getValueString(Integer.class, this::getDistance, level)),
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
        this.handleEffect(player, level);

        new SoundCreator(Sound.ENDERMAN_TELEPORT, 1.0F, 0.6F).play(player.getLocation());

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    private void handleEffect(final Player player, final int level) {
        final Vector playerVector = UtilJava.get(player.getLocation().toVector(), vector -> {
            vector.setY(vector.getY() + this.playerVectorY);
            return vector;
        });

        int count = 0;

        for (final Player targetPlayer : UtilEntity.getNearbyEntities(Player.class, player.getLocation(), this.getDistance(level))) {
            if (targetPlayer == player) {
                continue;
            }

            final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, player, targetPlayer);
            UtilServer.callEvent(friendlyFireEvent);
            if (friendlyFireEvent.isCancelled()) {
                continue;
            }

            if (!(friendlyFireEvent.isVulnerable())) {
                UtilMessage.simpleMessage(targetPlayer, this.getModule().getName(), "<var> used <green><var></green>.", Arrays.asList(friendlyFireEvent.getPlayerName(), this.getDisplayName(level)));
                continue;
            }

            final Vector vector = targetPlayer.getLocation().toVector().subtract(playerVector).normalize().multiply(this.targetVectorMultiplier);

            UtilVelocity.velocity(targetPlayer, vector, this.strength, this.yBase, this.yAdd, this.yMax, this.groundBoost);

            UtilMessage.simpleMessage(targetPlayer, this.getModule().getName(), "<var> pulled you in with <green><var></green>.", Arrays.asList(friendlyFireEvent.getPlayerName(), this.getDisplayName(level)));

            count++;
        }

        if (count > 0) {
            UtilMessage.simpleMessage(player, this.getName(), "You have pulled in <yellow><var></yellow> nearby opponent(s).", Collections.singletonList(String.valueOf(count)));
        }
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 5;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = level - 1;

        return this.recharge - (value * 1000L);
    }
}