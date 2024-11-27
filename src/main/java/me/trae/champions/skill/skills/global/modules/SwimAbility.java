package me.trae.champions.skill.skills.global.modules;

import me.trae.api.champions.skill.events.SkillPreActivateEvent;
import me.trae.champions.effect.EffectManager;
import me.trae.champions.effect.types.Silenced;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.skills.global.Swim;
import me.trae.champions.skill.skills.global.modules.abstracts.GlobalAbility;
import me.trae.core.Core;
import me.trae.core.client.Client;
import me.trae.core.client.ClientManager;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.energy.EnergyManager;
import me.trae.core.recharge.RechargeManager;
import me.trae.core.utility.UtilBlock;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilServer;
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

import java.util.Collections;

public class SwimAbility extends GlobalAbility<Swim> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "8.0")
    public float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "350")
    private long recharge;

    @ConfigInject(type = Double.class, path = "Strength", defaultValue = "0.6")
    private double strength;

    @ConfigInject(type = Double.class, path = "yMax", defaultValue = "0.6")
    private double yMax;

    @ConfigInject(type = Double.class, path = "yAdd", defaultValue = "0.2")
    private double yAdd;

    @ConfigInject(type = Boolean.class, path = "groundBoost", defaultValue = "false")
    private boolean groundBoost;

    public SwimAbility(final SkillManager manager) {
        super(manager);
    }

    @Override
    public Class<Swim> getClassOfSkill() {
        return Swim.class;
    }

    private float getEnergy(final int level) {
        return this.energy;
    }

    private long getRecharge(final int level) {
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

        final Client client = this.getInstance(Core.class).getManagerByClass(ClientManager.class).getClientByPlayer(player);
        if (client == null) {
            return;
        }

        if (!(this.canSwim(player, client))) {
            return;
        }

        this.activateSwim(player, client);
    }

    private boolean canSwim(final Player player, final Client client) {
        final Swim skill = this.getSkillByPlayer(player);

        final int level = skill.getLevel(player);
        if (level == 0) {
            return false;
        }

        if (!(client.isAdministrating())) {
            if (UtilServer.getEvent(new SkillPreActivateEvent(this.getSkillByPlayer(player), player)).isCancelled()) {
                return false;
            }

            if (this.getInstance().getManagerByClass(EffectManager.class).getModuleByClass(Silenced.class).isUserByEntity(player)) {
                UtilMessage.simpleMessage(player, "Skill", "You cannot use <green><var></green> while silenced.", Collections.singletonList(this.getName()));
                return false;
            }

            final RechargeManager rechargeManager = this.getInstance(Core.class).getManagerByClass(RechargeManager.class);
            if (rechargeManager.isCooling(player, skill.getName(), false)) {
                return false;
            }

            if (!(this.getInstance(Core.class).getManagerByClass(EnergyManager.class).use(player, skill.getName(), this.getEnergy(level), true))) {
                return false;
            }


            return rechargeManager.add(player, skill.getName(), this.getRecharge(level), false);
        }

        return true;
    }

    private void activateSwim(final Player player, final Client client) {
        new SoundCreator(Sound.SPLASH, 0.3F, 2.0F).play(player.getLocation());

        player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, Material.WATER, 100);

        double strength = this.strength;
        double yAdd = this.yAdd;
        final double yMax = this.yMax;
        final boolean groundBoost = this.groundBoost;

        if (client.isAdministrating()) {
            strength += 0.2D;
            yAdd += 0.2D;
        }

        UtilVelocity.velocity(player, strength, yAdd, yMax, groundBoost);
    }
}