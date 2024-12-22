package me.trae.champions.skill.skills.mage.sword;

import me.trae.api.champions.skill.events.SkillLocationEvent;
import me.trae.champions.role.types.Mage;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.Core;
import me.trae.core.blockrestore.BlockRestore;
import me.trae.core.blockrestore.BlockRestoreManager;
import me.trae.core.blockrestore.events.BlockRestoreRemoveEvent;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.throwable.Throwable;
import me.trae.core.throwable.ThrowableManager;
import me.trae.core.throwable.events.ThrowableGroundedEvent;
import me.trae.core.utility.*;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class GlacialPrison extends ActiveSkill<Mage, SkillData> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "40.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "25_000")
    private long recharge;

    @ConfigInject(type = Double.class, path = "Item-Velocity", defaultValue = "1.3")
    private double itemVelocity;

    @ConfigInject(type = Integer.class, path = "Distance", defaultValue = "5")
    private int distance;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "5_000")
    private long duration;

    @ConfigInject(type = String.class, path = "Material", defaultValue = "ICE")
    private String material;

    public GlacialPrison(final Mage module) {
        super(module, ActiveSkillType.SWORD);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private long getDuration(final int level) {
        return this.duration;
    }

    private int getDistance(final int level) {
        return this.distance;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with a Sword to Activate.",
                "",
                "Launches an orb, trapping any players",
                String.format("within %s blocks of it in a prison of ice for %s", this.getDistance(level), UtilTime.getTime(this.getDuration(level))),
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

    private Material getMaterial() {
        try {
            return Material.valueOf(this.material);
        } catch (final Exception ignored) {
        }

        return Material.ICE;
    }

    @Override
    public void onActivate(final Player player, final int level) {
        final long duration = this.getDuration(level);

        final Throwable throwable = new Throwable(this.getName(), new ItemStack(this.getMaterial()), player, duration, player.getEyeLocation(), player.getEyeLocation().getDirection().multiply(this.itemVelocity));

        this.getInstanceByClass(Core.class).getManagerByClass(ThrowableManager.class).addThrowable(throwable);

        this.addUser(new SkillData(player, level, duration));

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onThrowableGrounded(final ThrowableGroundedEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.isThrowableName(this.getName()))) {
            return;
        }

        final SkillData data = this.getUserByPlayer(event.getThrowable().getThrowerPlayer());
        if (data == null) {
            return;
        }

        event.setCancelled(true);

        final BlockRestoreManager blockRestoreManager = this.getInstanceByClass(Core.class).getManagerByClass(BlockRestoreManager.class);

        for (final Block block : UtilBlock.getSphere(event.getLocation(), this.getDistance(data.getLevel()), true)) {
            if (!(UtilBlock.airFoliage(block.getType()))) {
                continue;
            }

            if (UtilServer.getEvent(new SkillLocationEvent(this, block.getLocation())).isCancelled()) {
                continue;
            }

            final BlockRestore blockRestore = new BlockRestore(this.getName(), block, this.getMaterial(), (byte) 0, this.getDuration(data.getLevel()) + UtilMath.getRandomNumber(Long.class, 1000L, 3000L)) {
                @Override
                public boolean isSaveToRepository() {
                    return true;
                }
            };

            blockRestoreManager.addBlockRestore(blockRestore);

            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, this.getMaterial(), 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockRestoreRemove(final BlockRestoreRemoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getBlockRestore().getName().equals(this.getName()))) {
            return;
        }

        final Location location = event.getBlockRestore().getLocation();

        location.getWorld().playEffect(location, Effect.WATERDRIP, 1);
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