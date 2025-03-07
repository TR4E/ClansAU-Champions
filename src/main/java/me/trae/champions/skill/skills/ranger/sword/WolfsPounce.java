package me.trae.champions.skill.skills.ranger.sword;

import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.api.damage.utility.UtilDamage;
import me.trae.champions.role.types.Ranger;
import me.trae.champions.skill.skills.ranger.sword.data.WolfsPounceData;
import me.trae.champions.skill.types.ChannelSkill;
import me.trae.champions.skill.types.models.ToggleSkill;
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
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;

public class WolfsPounce extends ChannelSkill<Ranger, WolfsPounceData> implements ToggleSkill<WolfsPounceData>, Listener, Updater {

    // To-Do: Needs a description and more "@ConfigInject" values

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "30.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "18_000")
    private long recharge;

    @ConfigInject(type = Integer.class, path = "Max-Charges", defaultValue = "100")
    private int maxCharges;

    @ConfigInject(type = Integer.class, path = "Incremented-Charges", defaultValue = "25")
    private int incrementedCharges;

    public WolfsPounce(final Ranger module) {
        super(module);
    }

    @Override
    public Class<WolfsPounceData> getClassOfData() {
        return WolfsPounceData.class;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[0];
    }

    private double formatCharges(final int charges) {
        return charges * 0.01D;
    }

    @Override
    public boolean isActive(final Player player, final WolfsPounceData data) {
        return data.isPounced();
    }

    @Override
    public void onActivate(final Player player, final int level) {
        this.addUser(new WolfsPounceData(player, level));
    }

    @Override
    public void onDeActivate(final Player player, final WolfsPounceData data) {
        if (data.isPounced()) {
            return;
        }

        data.setPounced(true);
        data.setUsing(false);

        final double formattedCharges = this.formatCharges(data.getCharges());

        new SoundCreator(Sound.WOLF_BARK, 1.0F, 0.8F + 1.2F * (float) formattedCharges).play(player.getLocation());

        UtilVelocity.velocity(player, 0.4D + 1.4D * formattedCharges, 0.2D, 0.3D + 0.8D * formattedCharges, true);

        this.getInstanceByClass(Core.class).getManagerByClass(EffectManager.class).getModuleByClass(NoFall.class).addUser(new EffectData(player) {
            @Override
            public boolean isRemoveOnAction() {
                return true;
            }
        });

        UtilTitle.sendActionBar(player, "<green>Pounced!");

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(data.getLevel())));
    }

    @Override
    public void onUsing(final Player player, final WolfsPounceData data) {
        if (data.isPounced()) {
            return;
        }

        if (data.getCharges() >= this.maxCharges) {
            return;
        }

        if (!(UtilTime.elapsed(data.getLastUpdated(), 400L))) {
            return;
        }

        data.setCharges(data.getCharges() + this.incrementedCharges);

        data.updateLastUpdated();

        new SoundCreator(Sound.CLICK, 0.4F, 1.0F + (0.5F * data.getCharges())).play(player.getLocation());

        UtilMessage.simpleMessage(player, this.getName(), UtilString.pair("Charges", UtilString.format("<yellow>%s", data.getCharges())));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCustomPostDamage(final CustomPostDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamagee() instanceof Player)) {
            return;
        }

        final Player damagee = event.getDamageeByClass(Player.class);

        if (!(this.isUserByPlayer(damagee))) {
            return;
        }

        final WolfsPounceData data = this.getUserByPlayer(damagee);

        this.removeUser(damagee);
        this.reset(damagee);

        new SoundCreator(Sound.WOLF_WHINE, 0.6F, 1.2F).play(damagee.getLocation());

        UtilMessage.simpleMessage(damagee, this.getModule().getName(), "<green><var></green> was interrupted.", Collections.singletonList(this.getDisplayName(data.getLevel())));
    }

    @Update(delay = 250L)
    public void onUpdater() {
        this.getUsers().values().removeIf(data -> {
            if (!(data.isPounced())) {
                return false;
            }

            final Player player = Bukkit.getPlayer(data.getUUID());
            if (player == null) {
                return false;
            }

            for (final LivingEntity targetEntity : UtilEntity.getNearbyEntities(LivingEntity.class, player.getLocation(), 2.0F)) {
                if (targetEntity == player) {
                    continue;
                }

                if (targetEntity instanceof Player) {
                    final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, player, UtilJava.cast(Player.class, targetEntity));
                    UtilServer.callEvent(friendlyFireEvent);
                    if (friendlyFireEvent.isCancelled()) {
                        continue;
                    }

                    if (!(friendlyFireEvent.isVulnerable())) {
                        continue;
                    }

                    UtilMessage.simpleMessage(player, this.getModule().getName(), "You hit <var> with <green><var></green>.", Arrays.asList(friendlyFireEvent.getTargetName(), this.getDisplayName(data.getLevel())));
                    UtilMessage.simpleMessage(targetEntity, this.getModule().getName(), "<var> hit you with <green><var></green>.", Arrays.asList(friendlyFireEvent.getPlayerName(), this.getDisplayName(data.getLevel())));
                } else {
                    UtilMessage.simpleMessage(player, this.getModule().getName(), "You hit a <yellow><var></yellow> with <green><var></green>.", Arrays.asList(targetEntity.getName(), this.getDisplayName(data.getLevel())));
                }

                final double damage = 6.0D * this.formatCharges(data.getCharges());

                new SoundCreator(Sound.WOLF_BARK, 0.5F, 0.5F).play(player.getLocation());

                UtilDamage.damage(targetEntity, player, EntityDamageEvent.DamageCause.CUSTOM, damage, this.getDisplayName(data.getLevel()), 100L);

                UtilEntity.givePotionEffect(targetEntity, PotionEffectType.SLOW, 1, 3_000L);

                return true;
            }

            return UtilTime.elapsed(data.getSystemTime(), 3_000L);
        });
    }

    @Override
    public float getEnergy(final int level) {
        return this.energy;
    }

    @Override
    public float getEnergyNeeded(final int level) {
        return 0.0F;
    }

    @Override
    public float getEnergyUsing(final int level) {
        return 0.0F;
    }

    @Override
    public long getRecharge(final int level) {
        return this.recharge;
    }
}