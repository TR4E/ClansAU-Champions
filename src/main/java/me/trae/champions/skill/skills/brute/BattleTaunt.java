package me.trae.champions.skill.skills.brute;

import me.trae.api.champions.skill.SkillFriendlyFireEvent;
import me.trae.champions.role.types.Brute;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.ChannelSkill;
import me.trae.core.utility.*;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class BattleTaunt extends ChannelSkill<Brute, SkillData> {

    public BattleTaunt(final Brute module) {
        super(module);

        this.addPrimitive("Distance", 4.0D);
        this.addPrimitive("Velocity", 0.3D);
        this.addPrimitive("Material", Material.DIAMOND_BLOCK);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    @Override
    public String[] getDescription(final int level) {
        final double distance = this.getPrimitiveCasted(Double.class, "Distance") + level;

        return new String[]{
                "Hold Block with a Sword to Channel.",
                "",
                String.format("While channelling, any enemies within <green>%s</green> blocks", distance),
                "are slowly pulled in towards you.",
//                "",
//                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString())),
//                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyString()))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        this.addUser(new SkillData(player, level));
    }

    @Override
    public void onUsing(final Player player, final SkillData data) {
        player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, Material.valueOf(this.getPrimitiveCasted(String.class, "Material")));

        for (double i = 0.0D; i < this.getPrimitiveCasted(Double.class, "Distance") + data.getLevel(); i++) {
            final Location location = player.getEyeLocation().add(player.getLocation().getDirection().multiply(i));

            this.pull(player, location, i);
        }
    }

    private void pull(final Player player, final Location location, final double distance) {
        for (final LivingEntity target : UtilEntity.getNearbyEntities(LivingEntity.class, location, distance)) {
            if (target == player) {
                continue;
            }

            if (UtilMath.offset(player.getLocation().toVector(), target.getLocation().toVector()) < 2.0D) {
                continue;
            }

            if (target instanceof Player) {
                final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, player, UtilJava.cast(Player.class, target));
                UtilServer.callEvent(friendlyFireEvent);
                if (friendlyFireEvent.isCancelled() || !(friendlyFireEvent.isVulnerable())) {
                    continue;
                }
            }

            UtilVelocity.velocity(target, UtilVelocity.getTrajectory(target.getLocation().toVector(), player.getLocation().toVector()), this.getPrimitiveCasted(Double.class, "Velocity"), 0.0D, 0.0D, 1.0D, true);
        }
    }
}