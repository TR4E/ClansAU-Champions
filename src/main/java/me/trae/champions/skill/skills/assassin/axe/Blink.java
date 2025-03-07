package me.trae.champions.skill.skills.assassin.axe;

import me.trae.champions.role.types.Assassin;
import me.trae.champions.skill.skills.assassin.axe.data.BlinkData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.champions.skill.types.models.ToggleSkill;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.*;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collections;

public class Blink extends ActiveSkill<Assassin, BlinkData> implements ToggleSkill<BlinkData> {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "35.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "13_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "De-Blink-Duration", defaultValue = "5_000")
    private long deBlinkDuration;

    @ConfigInject(type = Integer.class, path = "Max-Range", defaultValue = "16")
    private int maxRange;

    public Blink(final Assassin module) {
        super(module, ActiveSkillType.AXE);
    }

    @Override
    public Class<BlinkData> getClassOfData() {
        return BlinkData.class;
    }

    private long getDeBlinkDuration(final int level) {
        return this.deBlinkDuration;
    }

    private int getMaxRange(final int level) {
        return this.maxRange;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with an Axe to Activate.",
                "",
                UtilString.format("Instantly teleport forwards %s blocks.", this.getValueString(Integer.class, this::getMaxRange, level)),
                "",
                UtilString.format("Using again within %s De-Blinks,", this.getValueString(Long.class, this::getDeBlinkDuration, level)),
                "",
                UtilString.pair("<gray>Recharge", UtilString.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", UtilString.format("<green>%s", this.getEnergyString(level)))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        final BlinkData data = new BlinkData(player, level, this.getDeBlinkDuration(level));

        Location lastLocation = player.getLocation();

        data.setLastLocation(lastLocation);

        double curRange = 0.0D;

        while (curRange <= this.getMaxRange(level)) {
            final Vector vector = player.getLocation().getDirection().multiply(curRange);

            final Location newTarget = player.getLocation().add(new Vector(0.0D, 0.2D, 0.0D)).add(vector);

            if (!(UtilBlock.airFoliage(newTarget.getBlock().getType()) || !(UtilBlock.airFoliage(newTarget.getBlock().getRelative(BlockFace.UP).getType())))) {
                break;
            }

            curRange += 0.1D;

            if (!(lastLocation.equals(newTarget))) {
                lastLocation.getWorld().playEffect(lastLocation, Effect.SMOKE, 4);
            }

            lastLocation = newTarget;
        }

        curRange -= 1.0D;

        if (curRange < 0.0D) {
            curRange = 0.0D;
        }

        final Location newLocation = player.getLocation().add(player.getLocation().getDirection().multiply(curRange).add(new Vector(0.0D, 0.4D, 0.0D)));

        if (curRange > 0.0D) {
            data.updateLastUpdated();

            player.teleport(newLocation);
        }

        this.addUser(data);

        player.getWorld().playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 0);

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(data.getLevel())));
    }

    @Override
    public void onDeActivate(final Player player, final BlinkData data) {
        Location currentLocation = player.getLocation();

        final Location newLocation = data.getLastLocation();

        double curRange = 0.0D;

        boolean done = false;

        while (!(done)) {
            final Vector vector = UtilVelocity.getTrajectory(player.getLocation().toVector(), newLocation.toVector());

            final Location newTarget = player.getLocation().add(vector.multiply(curRange));

            curRange += 0.2D;

            if (!(currentLocation.equals(newTarget))) {
                currentLocation.getWorld().playEffect(currentLocation, Effect.SMOKE, 4);
            }

            currentLocation = newTarget;

            if (UtilMath.offset(newTarget.toVector(), newLocation.toVector()) < 0.4D) {
                done = true;
            }

            if (curRange > 24.0D) {
                done = true;
            }
        }

        player.teleport(newLocation);

        player.getWorld().playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 0, 15);

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green>De-<var></green>.", Collections.singletonList(this.getDisplayName(data.getLevel())));
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (int) ((level - 1) * 1.5D);

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = (level - 1) * 2;

        return this.recharge - (value * 1000L);
    }
}