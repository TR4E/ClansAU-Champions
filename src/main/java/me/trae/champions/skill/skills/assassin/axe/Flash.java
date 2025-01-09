package me.trae.champions.skill.skills.assassin.axe;

import me.trae.champions.role.types.Assassin;
import me.trae.champions.skill.skills.assassin.axe.data.FlashData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.Core;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.energy.EnergyManager;
import me.trae.core.recharge.RechargeManager;
import me.trae.core.updater.annotations.Update;
import me.trae.core.updater.interfaces.Updater;
import me.trae.core.utility.*;
import me.trae.core.utility.components.SelfManagedAbilityComponent;
import me.trae.core.utility.objects.SoundCreator;
import me.trae.core.utility.particle.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Flash extends ActiveSkill<Assassin, FlashData> implements SelfManagedAbilityComponent, Updater {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "17.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "11_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Last-Updated-Duration", defaultValue = "9_000")
    private long lastUpdatedDuration;

    @ConfigInject(type = Double.class, path = "Max-Range", defaultValue = "8.0")
    private double maxRange;

    public Flash(final Assassin module) {
        super(module, ActiveSkillType.AXE);
    }

    @Override
    public Class<FlashData> getClassOfData() {
        return FlashData.class;
    }

    private long getLastUpdatedDuration(final int level) {
        return this.lastUpdatedDuration - (level * 1000L);
    }

    private double getMaxRange(final int level) {
        return this.maxRange;
    }

    private int getMaxCharges(final int level) {
        return level;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with an Axe to Activate.",
                "",
                String.format("Instantly teleport forwards %s blocks.", this.getMaxRange(level)),
                "",
                String.format("Store up to <green>%s</green> charges.", this.getMaxCharges(level)),
                String.format("One charge per <green>%s</green>.", UtilTime.getTime(this.getLastUpdatedDuration(level))),
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyString(level)))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        final FlashData data = this.getUserByPlayer(player);
        if (data == null) {
            return;
        }

        if (data.getCharges() == 0) {
            UtilMessage.simpleMessage(player, this.getName(), "You have no charges left.");
            return;
        }

        final RechargeManager rechargeManager = this.getInstanceByClass(Core.class).getManagerByClass(RechargeManager.class);
        final EnergyManager energyManager = this.getInstanceByClass(Core.class).getManagerByClass(EnergyManager.class);

        if (this.hasRecharge(level)) {
            if (rechargeManager.isCooling(player, this.getName(), true)) {
                return;
            }

            if (energyManager.isExhausted(player, this.getName(), this.getEnergy(level), true)) {
                return;
            }
        }

        double curRange = 0.0D;

        while (curRange <= this.getMaxRange(data.getLevel())) {
            final Location newTarget = player.getLocation().add(new Vector(0.0D, 0.2D, 0.0D)).add(player.getLocation().getDirection().multiply(curRange));

            if (!(UtilBlock.airFoliage(newTarget.getBlock().getType()) || !(UtilBlock.airFoliage(newTarget.getBlock().getRelative(BlockFace.UP).getType())))) {
                break;
            }

            curRange += 0.2D;
            ParticleEffect.FIREWORKS_SPARK.display(0.0F, 0.5F, 0.0F, 1.0F, 1, newTarget, 128.0F);
        }

        curRange -= 0.4D;

        if (curRange < 0.0D) {
            curRange = 0.0D;
        }

        final Location location = player.getLocation().add(player.getLocation().getDirection().multiply(curRange).add(new Vector(0.0D, 0.4D, 0.0D)));
        if (curRange > 0.0D) {
            player.teleport(location);
        }

        new SoundCreator(Sound.WITHER_SHOOT, 0.4F, 1.2F).play(player.getLocation());
        new SoundCreator(Sound.SILVERFISH_KILL, 1.0F, 1.6F).play(player.getLocation());


        data.setCharges(Math.max(0, data.getCharges() - 1));

        this.displayCharges(player, data);

        energyManager.use(player, this.getName(), this.getEnergy(level), true);

        data.updateLastUpdated();

        if (data.getCharges() == 0) {
            rechargeManager.add(player, this.getName(), this.getRecharge(level), true);
        }
    }

    private void displayCharges(final Player player, final FlashData data) {
        UtilMessage.simpleMessage(player, this.getName(), UtilString.pair("Charges", UtilString.format("<yellow>%s", data.getCharges())));
    }

    @Update(delay = 50L)
    public void onUpdater() {
        for (final Player player : this.getModule().getUsers()) {
            final int level = this.getLevel(player);
            if (level == 0) {
                continue;
            }

            if (!(this.isUserByPlayer(player))) {
                this.addUser(new FlashData(player, level));
            }

            final FlashData data = this.getUserByPlayer(player);

            if (data.getLevel() != level) {
                data.setLevel(level);
            }

            if (data.getCharges() >= this.getMaxCharges(data.getLevel())) {
                continue;
            }

            if (!(UtilTime.elapsed(data.getLastUpdated(), this.getLastUpdatedDuration(data.getLevel())))) {
                continue;
            }

            data.updateLastUpdated();

            data.setCharges(data.getCharges() + 1);

            this.displayCharges(player, data);
        }
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