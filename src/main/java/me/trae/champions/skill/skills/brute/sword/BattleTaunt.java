package me.trae.champions.skill.skills.brute.sword;

import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.champions.role.types.Brute;
import me.trae.champions.skill.data.types.ChannelSkillData;
import me.trae.champions.skill.types.ChannelSkill;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.*;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class BattleTaunt extends ChannelSkill<Brute, ChannelSkillData> {

    @ConfigInject(type = Float.class, path = "Energy-Needed", defaultValue = "20.0")
    private float energyNeeded;

    @ConfigInject(type = Float.class, path = "Energy-Using", defaultValue = "3.0")
    private float energyUsing;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "5_000")
    private long recharge;

    @ConfigInject(type = Double.class, path = "Velocity", defaultValue = "0.3")
    private double velocity;

    @ConfigInject(type = String.class, path = "Material", defaultValue = "DIAMOND_BLOCK")
    private String material;

    public BattleTaunt(final Brute module) {
        super(module);
    }

    @Override
    public Class<ChannelSkillData> getClassOfData() {
        return ChannelSkillData.class;
    }

    private int getDistance(final int level) {
        return level * 2;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Hold Block with a Sword to Channel.",
                "",
                String.format("While channelling, any enemies within <green>%s</green> blocks", this.getDistance(level)),
                "are slowly pulled in towards you.",
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyUsing(level)))
        };
    }

    @Override
    public int getDefaultLevel() {
        return 2;
    }

    @Override
    public void onActivate(final Player player, final int level) {
        this.addUser(new ChannelSkillData(player, level));
    }

    @Override
    public void onUsing(final Player player, final ChannelSkillData data) {
        player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, Material.valueOf(this.material));

        for (double i = 0.0D; i < this.getDistance(data.getLevel()) + data.getLevel(); i++) {
            final Location location = player.getEyeLocation().add(player.getLocation().getDirection().multiply(i));

            this.pull(player, location, i);
        }
    }

    private void pull(final Player player, final Location location, final double distance) {
        for (final LivingEntity targetPlayer : UtilEntity.getNearbyEntities(LivingEntity.class, location, distance)) {
            if (targetPlayer == player) {
                continue;
            }

            if (UtilMath.offset(player.getLocation().toVector(), targetPlayer.getLocation().toVector()) < 2.0D) {
                continue;
            }

            if (targetPlayer instanceof Player) {
                final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, player, UtilJava.cast(Player.class, targetPlayer));
                UtilServer.callEvent(friendlyFireEvent);
                if (friendlyFireEvent.isCancelled() || !(friendlyFireEvent.isVulnerable())) {
                    continue;
                }
            }

            UtilVelocity.velocity(targetPlayer, UtilVelocity.getTrajectory(targetPlayer.getLocation().toVector(), player.getLocation().toVector()), this.velocity, 0.0D, 0.0D, 1.0D, true);
        }
    }

    @Override
    public float getEnergy(final int level) {
        return 0.0F;
    }

    @Override
    public float getEnergyNeeded(final int level) {
        return this.energyNeeded;
    }

    @Override
    public float getEnergyUsing(final int level) {
        return this.energyUsing;
    }

    @Override
    public long getRecharge(final int level) {
        return this.recharge;
    }
}