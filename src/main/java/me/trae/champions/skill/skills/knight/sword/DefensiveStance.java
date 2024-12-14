package me.trae.champions.skill.skills.knight.sword;

import me.trae.api.damage.events.damage.CustomDamageEvent;
import me.trae.api.damage.utility.UtilDamage;
import me.trae.champions.role.types.Knight;
import me.trae.champions.skill.data.types.ChannelSkillData;
import me.trae.champions.skill.types.ChannelSkill;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.UtilMath;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.UtilVelocity;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class DefensiveStance extends ChannelSkill<Knight, ChannelSkillData> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy-Needed", defaultValue = "12.0")
    private float energyNeeded;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "3_000")
    private long recharge;

    @ConfigInject(type = String.class, path = "Material", defaultValue = "GLASS")
    private String material;

    public DefensiveStance(final Knight module) {
        super(module);
    }

    @Override
    public Class<ChannelSkillData> getClassOfData() {
        return ChannelSkillData.class;
    }

    private Material getMaterial() {
        try {
            return Material.valueOf(this.material);
        } catch (final Exception ignored) {
        }

        return Material.GLASS;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Hold Block with a Sword to Channel",
                "",
                "While active, you are immune to all",
                "melee damage from attacks infront of you.",
                "",
                "Players who attack you, receive damage",
                "and get knocked back.",
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyUsingString(level)))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        this.addUser(new ChannelSkillData(player, level));
    }

    @Override
    public void onUsing(final Player player, final ChannelSkillData data) {
        player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, this.getMaterial());
    }

    @EventHandler
    public void onCustomDamage(final CustomDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        if (!(event.getDamagee() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        final Player damager = event.getDamagerByClass(Player.class);
        final Player damagee = event.getDamageeByClass(Player.class);

        final ChannelSkillData data = this.getUserByPlayer(damagee);
        if (data == null) {
            return;
        }

        if (!(damagee.isBlocking())) {
            return;
        }

        if (UtilMath.offset(damagee.getLocation().getDirection(), UtilVelocity.getTrajectory(damagee.getLocation().toVector(), damager.getLocation().toVector())) > 0.6D) {
            return;
        }

        event.setCancelled(true);

        damager.setVelocity(damagee.getEyeLocation().getDirection().add(new Vector(0.0D, 0.5D, 0.0D)).multiply(1.0D));

        UtilDamage.damage(damager, damagee, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 2.0D, this.getDisplayName(data.getLevel()), 1000L);

        new SoundCreator(Sound.ZOMBIE_WOODBREAK, 1.0F, 2.0F).play(damagee.getLocation());

        UtilMessage.simpleMessage(damager, this.getName(), "<var> is invulnerable to melee attacks for <green><var></green>.", Arrays.asList(event.getDamageeName(), data.getRemainingString()));
    }

    @Override
    public float getEnergy(final int level) {
        return 0.0F;
    }

    @Override
    public float getEnergyNeeded(final int level) {
        final int value = level - 1;

        return this.energyNeeded - value;
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