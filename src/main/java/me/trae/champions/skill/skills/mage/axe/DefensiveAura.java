package me.trae.champions.skill.skills.mage.axe;

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
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;

public class DefensiveAura extends ActiveSkill<Mage, SkillData> {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "30.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "30_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "10_000")
    private long duration;

    public DefensiveAura(final Mage module) {
        super(module, ActiveSkillType.AXE);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private int getDistance(final int level) {
        return level * 2;
    }

    @Override
    public String[] getDescription(final int level) {
        final int amplifier = level;

        return new String[]{
                "Right-Click with an Axe to Activate.",
                "",
                String.format("Gives you and all allies within <green>%s</green> blocks", this.getDistance(level)),
                String.format("<green>%s</green> bonus hearts.", amplifier),
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
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            UtilEntity.removePotionEffect(player, PotionEffectType.HEALTH_BOOST);
        }

        super.reset(player);
    }

    @Override
    public void onActivate(final Player player, final int level) {
        new SoundCreator(Sound.ZOMBIE_UNFECT, 2.0F, 2.0F).play(player.getLocation());

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));

        this.handleEffect(player, level);
    }

    private void handleEffect(final Player player, final int level) {
        int count = 0;

        for (final Player targetPlayer : UtilEntity.getNearbyEntities(Player.class, player.getLocation(), this.getDistance(level))) {
            final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, player, targetPlayer);
            UtilServer.callEvent(friendlyFireEvent);
            if (friendlyFireEvent.isCancelled() || friendlyFireEvent.isVulnerable()) {
                continue;
            }

            UtilEntity.givePotionEffect(targetPlayer, PotionEffectType.HEALTH_BOOST, level, this.duration);

            targetPlayer.setHealth(UtilMath.getMinAndMax(Double.class, 0.0D, targetPlayer.getMaxHealth(), targetPlayer.getHealth() + level * 2));

            if (targetPlayer != player) {
                UtilMessage.simpleMessage(targetPlayer, this.getModule().getName(), "<var> used <green><var></green>.", Arrays.asList(friendlyFireEvent.getPlayerName(), this.getDisplayName(level)));

                count++;
            }
        }

        if (count > 0) {
            UtilMessage.simpleMessage(player, this.getName(), "You have assisted <yellow><var></yellow> nearby teammate(s).", Collections.singletonList(String.valueOf(count)));
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