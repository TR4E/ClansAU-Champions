package me.trae.champions.skill.skills.mage.passive_b;

import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.api.champions.skill.events.SkillLocationEvent;
import me.trae.champions.role.types.Mage;
import me.trae.champions.skill.data.types.ToggleUpdaterDropSkillData;
import me.trae.champions.skill.types.ToggleUpdaterDropSkill;
import me.trae.core.Core;
import me.trae.core.blockrestore.BlockRestore;
import me.trae.core.blockrestore.BlockRestoreManager;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.*;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class ArcticArmour extends ToggleUpdaterDropSkill<Mage, ToggleUpdaterDropSkillData> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "4.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "5_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "2_000")
    private long duration;

    @ConfigInject(type = Integer.class, path = "Distance", defaultValue = "2")
    private int distance;

    @ConfigInject(type = Integer.class, path = "Resistance-Amplifier", defaultValue = "1")
    private int resistanceAmplifier;

    @ConfigInject(type = Long.class, path = "Resistance-Duration", defaultValue = "2_000")
    private long resistanceDuration;

    @ConfigInject(type = Integer.class, path = "Slowness-Amplifier", defaultValue = "1")
    private int slownessAmplifier;

    @ConfigInject(type = Long.class, path = "Slowness-Duration", defaultValue = "2_000")
    private long slownessDuration;

    public ArcticArmour(final Mage module) {
        super(module);
    }

    @Override
    public Class<ToggleUpdaterDropSkillData> getClassOfData() {
        return ToggleUpdaterDropSkillData.class;
    }

    private int getDistance(final int level) {
        return this.distance + level;
    }

    private int getResistanceAmplifier(final int level) {
        return this.resistanceAmplifier;
    }

    private int getSlownessAmplifier(final int level) {
        return this.slownessAmplifier;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Drop Sword/Axe to Toggle.",
                "",
                "Create a freezing area around you",
                String.format("in a <green>%s</green> block radius.", this.getDistance(level)),
                "",
                String.format("Allies inside this area receive Resistance %s,", this.getResistanceAmplifier(level)),
                String.format("while enemies receive Slowness %s.", this.getSlownessAmplifier(level)),
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyUsingString(level)))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        this.addUser(new ToggleUpdaterDropSkillData(player, level));

        UtilMessage.simpleMessage(player, this.getModule().getName(), UtilString.pair(this.getName(), "<green>On"));
    }

    @Override
    public void onDeActivate(final Player player, final ToggleUpdaterDropSkillData data) {
        UtilMessage.simpleMessage(player, this.getModule().getName(), UtilString.pair(this.getName(), "<red>Off"));
    }

    @Override
    public void onUsing(final Player player, final ToggleUpdaterDropSkillData data) {
        new SoundCreator(Sound.AMBIENCE_RAIN, 0.3F, 0.0F).play(player.getLocation());

        for (final LivingEntity targetEntity : UtilEntity.getNearbyEntities(LivingEntity.class, player.getLocation(), this.getDistance(data.getLevel()))) {
            if (UtilServer.getEvent(new SkillLocationEvent(this, targetEntity.getLocation())).isCancelled()) {
                continue;
            }

            if (targetEntity instanceof Player) {
                final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, player, UtilJava.cast(Player.class, targetEntity));
                UtilServer.callEvent(friendlyFireEvent);
                if (friendlyFireEvent.isCancelled()) {
                    continue;
                }

                if (!(friendlyFireEvent.isVulnerable())) {
                    UtilEntity.givePotionEffect(targetEntity, PotionEffectType.DAMAGE_RESISTANCE, this.getResistanceAmplifier(data.getLevel()), this.resistanceDuration);
                    continue;
                }
            }

            UtilEntity.givePotionEffect(targetEntity, PotionEffectType.SLOW, this.getSlownessAmplifier(data.getLevel()), this.slownessDuration);
        }

        final BlockRestoreManager blockRestoreManager = this.getInstanceByClass(Core.class).getManagerByClass(BlockRestoreManager.class);

        for (final Block block : UtilBlock.getInRoundedRadius(player.getLocation(), this.getDistance(data.getLevel()), this.getDistance(data.getLevel()))) {
            if (block.getLocation().getY() > player.getLocation().getY()) {
                continue;
            }

            if (block.getType() != Material.AIR) {
                continue;
            }

            if (UtilServer.getEvent(new SkillLocationEvent(this, block.getLocation())).isCancelled()) {
                continue;
            }

            final Block relativeDownBlock = block.getRelative(BlockFace.DOWN);

            final BlockRestore oldBlockRestore = blockRestoreManager.getBlockRestoreByLocation(relativeDownBlock.getLocation());
            if (oldBlockRestore != null && !(oldBlockRestore.getName().equals(this.getName()))) {
                continue;
            }

            final boolean isValidForIce = relativeDownBlock.getType() == Material.ICE || Arrays.asList(Material.STATIONARY_WATER, Material.WATER).contains(relativeDownBlock.getType());

            if (isValidForIce) {
                final BlockRestore blockRestore = new BlockRestore(this.getName(), relativeDownBlock, Material.ICE, (byte) 0, this.duration + UtilMath.getRandomNumber(Long.class, 1000L, 3000L)) {
                    @Override
                    public boolean isSaveToRepository() {
                        return true;
                    }
                };

                blockRestoreManager.addBlockRestore(blockRestore);
                continue;
            }

            final boolean isValidForSnow = relativeDownBlock.getType() == Material.SNOW_BLOCK || (UtilBlock.isDirtRelatedBlock(relativeDownBlock) || UtilBlock.isStoneRelatedBlock(relativeDownBlock) || UtilBlock.isWoodRelatedBlock(relativeDownBlock));

            if (isValidForSnow) {
                final BlockRestore blockRestore = new BlockRestore(this.getName(), relativeDownBlock, Material.SNOW_BLOCK, (byte) 0, this.duration + UtilMath.getRandomNumber(Long.class, 1000L, 3000L)) {
                    @Override
                    public boolean isSaveToRepository() {
                        return true;
                    }
                };

                blockRestoreManager.addBlockRestore(blockRestore);
            }
        }
    }

    @Override
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            UtilEntity.removePotionEffect(player, PotionEffectType.DAMAGE_RESISTANCE);
        }

        super.reset(player);
    }

    @Override
    public float getEnergy(final int level) {
        return 0.0F;
    }

    @Override
    public float getEnergyNeeded(final int level) {
        return this.energy;
    }

    @Override
    public float getEnergyUsing(final int level) {
        return this.getEnergyNeeded(level) / 2;
    }

    @Override
    public long getRecharge(final int level) {
        return this.recharge;
    }
}