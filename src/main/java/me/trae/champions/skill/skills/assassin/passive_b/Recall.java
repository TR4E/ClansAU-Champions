package me.trae.champions.skill.skills.assassin.passive_b;

import me.trae.champions.role.types.Assassin;
import me.trae.champions.skill.skills.assassin.passive_b.data.RecallData;
import me.trae.champions.skill.types.DropSkill;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.updater.annotations.Update;
import me.trae.core.updater.interfaces.Updater;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.UtilTime;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collections;

public class Recall extends DropSkill<Assassin, RecallData> implements Updater {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "85.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "45_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "3_000")
    private long duration;

    @ConfigInject(type = String.class, path = "Material", defaultValue = "EMERALD_BLOCK")
    private String material;

    public Recall(final Assassin module) {
        super(module);
    }

    @Override
    public Class<RecallData> getClassOfData() {
        return RecallData.class;
    }

    private long getDuration(final int level) {
        return this.duration;
    }

    private Material getMaterial() {
        try {
            return Material.valueOf(this.material);
        } catch (final Exception ignored) {
        }

        return Material.EMERALD_BLOCK;
    }

    @Override
    public String[] getDescription(final int level) {
        final String durationString = this.getValueString(Long.class, this::getDuration, level);

        return new String[]{
                "Drop Sword/Axe to Activate.",
                "",
                "Teleports you back to where you",
                String.format("were located %s ago.", durationString),
                "",
                "Increases health by 1/4 of the health",
                String.format("you had %s ago.", durationString),
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyString(level)))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        final RecallData data = this.getUserByPlayer(player);
        if (data == null) {
            return;
        }

        if (data.getLocation() == null) {
            UtilMessage.simpleMessage(player, this.getModule().getName(), "You need to wait to use <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
            return;
        }

        new SoundCreator(Sound.ZOMBIE_UNFECT, 2.0F, 2.0F).play(player.getLocation());

        player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, this.getMaterial());

        player.teleport(data.getLocation());

        player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + (data.getHealth() / 4)));

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @Update(delay = 250L)
    public void onUpdater() {
        for (final Player player : this.getModule().getPlayers()) {
            final int level = this.getLevel(player);
            if (level == 0) {
                continue;
            }

            if (!(this.isUserByPlayer(player))) {
                this.addUser(new RecallData(player, level));
            }

            final RecallData data = this.getUserByPlayer(player);

            if (data.getLevel() != level) {
                data.setLevel(level);
            }

            if (!(UtilTime.elapsed(data.getLastUpdated(), this.getDuration(data.getLevel())))) {
                continue;
            }

            data.update(player);
        }
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