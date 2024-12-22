package me.trae.champions.skill.skills.global.modules;

import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.skills.global.Swim;
import me.trae.champions.skill.skills.global.modules.abstracts.GlobalAbility;
import me.trae.core.Core;
import me.trae.core.client.ClientManager;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.UtilBlock;
import me.trae.core.utility.UtilVelocity;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class SwimAbility extends GlobalAbility<Swim> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "10.0")
    public float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "350")
    private long recharge;

    @ConfigInject(type = Double.class, path = "Strength", defaultValue = "0.6")
    private double strength;

    @ConfigInject(type = Double.class, path = "yAdd", defaultValue = "0.2")
    private double yAdd;

    @ConfigInject(type = Double.class, path = "yMax", defaultValue = "0.6")
    private double yMax;

    @ConfigInject(type = Boolean.class, path = "groundBoost", defaultValue = "false")
    private boolean groundBoost;

    public SwimAbility(final SkillManager manager) {
        super(manager);
    }

    @Override
    public Class<Swim> getClassOfSkill() {
        return Swim.class;
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 2;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        return this.recharge;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerToggleSneak(final PlayerToggleSneakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        if (!(this.isSkillByPlayer(player))) {
            return;
        }

        if (!(UtilBlock.isInWater(player.getLocation()))) {
            return;
        }

        if (player.isSneaking()) {
            return;
        }

        final Swim skill = this.getSkillByPlayer(player);
        if (skill == null) {
            return;
        }

        if (!(this.canActivate(player, skill))) {
            return;
        }

        this.activateSwim(player);
    }

    private void activateSwim(final Player player) {
        new SoundCreator(Sound.SPLASH, 0.3F, 2.0F).play(player.getLocation());

        player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, Material.WATER, 100);

        double strength = this.strength;
        double yAdd = this.yAdd;
        final double yMax = this.yMax;
        final boolean groundBoost = this.groundBoost;

        if (this.getInstanceByClass(Core.class).getManagerByClass(ClientManager.class).getClientByPlayer(player).isAdministrating()) {
            strength += 0.2D;
            yAdd += 0.2D;
        }

        UtilVelocity.velocity(player, strength, yAdd, yMax, groundBoost);
    }
}