package me.trae.champions.skill.skills.ranger.bow;

import me.trae.champions.role.types.Ranger;
import me.trae.champions.skill.types.ActiveBowSkill;
import me.trae.champions.skill.types.data.BowSkillData;
import me.trae.core.Core;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.effect.EffectManager;
import me.trae.core.effect.data.EffectData;
import me.trae.core.effect.types.NoFall;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.UtilVelocity;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

public class RopedArrow extends ActiveBowSkill<Ranger, BowSkillData> {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "30.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "17_000")
    private long recharge;

    @ConfigInject(type = Double.class, path = "Strength", defaultValue = "2.5")
    private double strength;

    @ConfigInject(type = Double.class, path = "yBase", defaultValue = "0.4")
    private double yBase;

    @ConfigInject(type = Double.class, path = "yAdd", defaultValue = "0.3")
    private double yAdd;

    @ConfigInject(type = Double.class, path = "yMax", defaultValue = "1.5")
    private double yMax;

    @ConfigInject(type = Boolean.class, path = "groundBoost", defaultValue = "true")
    private boolean groundBoost;

    public RopedArrow(final Ranger module) {
        super(module);
    }

    @Override
    public Class<BowSkillData> getClassOfData() {
        return BowSkillData.class;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Left-Click with a Bow to Prepare.",
                "",
                "Your next arrow will pull you",
                "in after it hits.",
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
        this.addUser(new BowSkillData(player, level));

        super.onActivate(player, level);
    }

    @Override
    public void onHitByLocation(final Player player, final Location location, final BowSkillData data) {
        final Arrow arrow = data.getArrow();
        if (arrow == null) {
            return;
        }

        this.getInstanceByClass(Core.class).getManagerByClass(EffectManager.class).getModuleByClass(NoFall.class).addUser(new EffectData(player) {
            @Override
            public boolean isRemoveOnAction() {
                return true;
            }
        });

        new SoundCreator(Sound.BLAZE_BREATH, 2.5F, 2.0F).play(player.getLocation());

        UtilJava.call(UtilVelocity.getTrajectory(player.getLocation().toVector(), arrow.getLocation().toVector()), trajectory -> {
            final double multiplier = arrow.getVelocity().length() / 3.0D;

            UtilVelocity.velocity(player, trajectory, this.strength + multiplier, this.yBase, this.yAdd * multiplier, this.yMax * multiplier, this.groundBoost);
        });
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 2;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = level - 1;

        return this.recharge - (value * 1000L);
    }
}