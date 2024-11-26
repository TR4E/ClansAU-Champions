package me.trae.champions.skill.skills.mage.passive_b;

import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.champions.role.types.Mage;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.updater.annotations.Update;
import me.trae.core.updater.interfaces.Updater;
import me.trae.core.utility.UtilEntity;
import me.trae.core.utility.UtilServer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class HolyLight extends PassiveSkill<Mage, SkillData> implements Updater {

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "6_000")
    private long duration;

    public HolyLight(final Mage module) {
        super(module, PassiveSkillType.PASSIVE_B);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private int getAmplifier(final int level) {
        return level;
    }

    private int getDistance(final int level) {
        return level * 3;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Create an aura that gives",
                "yourself and all allies within",
                String.format("<green>%s</green> blocks, Regeneration <green>%s</green>.", this.getDistance(level), this.getAmplifier(level)),
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
            UtilEntity.removePotionEffect(player, PotionEffectType.REGENERATION);
        }

        super.reset(player);
    }

    @Update(delay = 250L)
    public void onUpdater() {
        for (final Player player : this.getModule().getUsers()) {
            if (!(this.isUserByPlayer(player))) {
                this.addUser(new SkillData(player, getLevel(player)));
            }

            final SkillData data = this.getUserByPlayer(player);

            this.giveEffect(player, data.getLevel(), Integer.MAX_VALUE);

            for (final Player targetPlayer : UtilEntity.getNearbyEntities(Player.class, player.getLocation(), this.getDistance(data.getLevel()))) {
                if (targetPlayer == player) {
                    continue;
                }

                final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, player, targetPlayer);
                UtilServer.callEvent(friendlyFireEvent);
                if (friendlyFireEvent.isCancelled() || friendlyFireEvent.isVulnerable()) {
                    continue;
                }

                this.giveEffect(player, data.getLevel(), this.duration);
            }
        }
    }

    private void giveEffect(final Player player, final int level, final long duration) {
        if (UtilEntity.hasPotionEffect(player, PotionEffectType.REGENERATION, this.getAmplifier(level))) {
            return;
        }

        UtilEntity.givePotionEffect(player, PotionEffectType.REGENERATION, this.getAmplifier(level), duration);
    }
}