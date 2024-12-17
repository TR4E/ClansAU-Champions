package me.trae.champions.skill.skills.brute.axe;

import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.api.champions.skill.events.SkillLocationEvent;
import me.trae.api.damage.utility.UtilDamage;
import me.trae.champions.Champions;
import me.trae.champions.role.types.Brute;
import me.trae.champions.skill.skills.brute.axe.data.SeismicSlamData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.Core;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.effect.EffectManager;
import me.trae.core.effect.data.EffectData;
import me.trae.core.effect.types.NoFall;
import me.trae.core.updater.annotations.Update;
import me.trae.core.updater.interfaces.Updater;
import me.trae.core.utility.*;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class SeismicSlam extends ActiveSkill<Brute, SeismicSlamData> implements Updater {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "50.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "25_000")
    private long recharge;

    @ConfigInject(type = Integer.class, path = "Block-Distance", defaultValue = "3")
    private int blockDistance;

    @ConfigInject(type = Double.class, path = "Entity-Distance", defaultValue = "4.0")
    private double entityDistance;

    @ConfigInject(type = Double.class, path = "Max-Damage", defaultValue = "6.0")
    private double maxDamage;

    @ConfigInject(type = Double.class, path = "Strength", defaultValue = "0.6")
    private double strength;

    @ConfigInject(type = Double.class, path = "yBase", defaultValue = "0.0")
    private double yBase;

    @ConfigInject(type = Double.class, path = "yAdd", defaultValue = "0.8")
    private double yAdd;

    @ConfigInject(type = Double.class, path = "yMax", defaultValue = "0.8")
    private double yMax;

    @ConfigInject(type = Boolean.class, path = "groundBoost", defaultValue = "true")
    private boolean groundBoost;

    @ConfigInject(type = Boolean.class, path = "Friendly-Fire", defaultValue = "false")
    private boolean friendlyFire;

    public SeismicSlam(final Brute module) {
        super(module, ActiveSkillType.AXE);
    }

    @Override
    public Class<SeismicSlamData> getClassOfData() {
        return SeismicSlamData.class;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with an Axe to Activate.",
                "",
                "Jump and slam the ground, knocking up all opponents",
                "within a small radius.",
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
    public void onActivate(final Player player, final int level) {
        player.setVelocity(new Vector(0.0D, 1.3D, 0.0D));

        final SeismicSlamData data = new SeismicSlamData(player, level);

        this.addUser(data);

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));

        UtilServer.runTaskLater(Champions.class, false, 15L, () -> {
            player.setVelocity(new Vector(0.0D, -1.0D, 0.0D));

            this.getInstance(Core.class).getManagerByClass(EffectManager.class).getModuleByClass(NoFall.class).addUser(new EffectData(player) {
                @Override
                public boolean isRemoveOnAction() {
                    return true;
                }
            });

            data.setHeight(player.getLocation().getY());
        });
    }

    @Update(delay = 50L)
    public void onUpdater() {
        this.getUsers().values().removeIf(data -> {
            final Player player = Bukkit.getPlayer(data.getUUID());
            if (player == null) {
                return true;
            }

            if (!(UtilTime.elapsed(data.getSystemTime(), 1000L))) {
                return false;
            }

            if (!(UtilBlock.isGrounded(player.getLocation()))) {
                return false;
            }

            int count = 0;

            for (final Map.Entry<LivingEntity, Double> entry : UtilEntity.getNearbyEntitiesWithDistance(LivingEntity.class, player.getLocation(), this.entityDistance).entrySet()) {
                final LivingEntity nearbyEntity = entry.getKey();
                final double nearbyEntityDistance = entry.getValue();

                if (nearbyEntity == player) {
                    continue;
                }

                if (nearbyEntity instanceof Player) {
                    final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, player, UtilJava.cast(Player.class, nearbyEntity));
                    UtilServer.callEvent(friendlyFireEvent);
                    if (friendlyFireEvent.isCancelled()) {
                        continue;
                    }

                    if (!(this.friendlyFire)) {
                        if (!(friendlyFireEvent.isVulnerable())) {
                            continue;
                        }
                    }

                    count++;

                    UtilMessage.simpleMessage(nearbyEntity, this.getModule().getName(), "<var> hit you with <green><var></green>.", Arrays.asList(friendlyFireEvent.getPlayerName(), this.getDisplayName(data.getLevel())));
                }

                UtilDamage.damage(nearbyEntity, player, EntityDamageEvent.DamageCause.CUSTOM, Math.min(this.maxDamage, data.getLevel() * nearbyEntityDistance + 0.5D), this.getDisplayName(data.getLevel()), 2000L);

                UtilVelocity.velocity(nearbyEntity, 0.3 * (data.getHeight() - player.getLocation().getY()) * 0.1D, 0.8D, 1.2D, true);
            }

            if (count > 0) {
                UtilMessage.simpleMessage(player, this.getModule().getName(), "You attacked <yellow><var></yellow> opponents with <green><var></green>.", Arrays.asList(String.valueOf(count), this.getDisplayName(data.getLevel())));
            }

            new SoundCreator(Sound.ZOMBIE_WOOD, 2.0F, 2.0F).play(player.getLocation());

            for (final Block block : UtilBlock.getInSquaredRadius(player.getLocation(), this.blockDistance)) {
                if (block.getType() == Material.AIR || UtilBlock.isInLiquid(block.getLocation())) {
                    continue;
                }

                if (UtilServer.getEvent(new SkillLocationEvent(this, block.getLocation())).isCancelled()) {
                    continue;
                }

                UtilBlock.playBrokenEffect(block);
            }

            return true;
        });
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