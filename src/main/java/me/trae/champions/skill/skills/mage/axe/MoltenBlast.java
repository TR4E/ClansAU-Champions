package me.trae.champions.skill.skills.mage.axe;

import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.api.damage.events.damage.CustomPreDamageEvent;
import me.trae.api.damage.utility.UtilDamage;
import me.trae.champions.role.types.Mage;
import me.trae.champions.skill.skills.mage.axe.data.MoltenBlastData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.*;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Arrays;
import java.util.Collections;

public class MoltenBlast extends ActiveSkill<Mage, MoltenBlastData> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "50.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "25_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "10_000")
    private long duration;

    @ConfigInject(type = Long.class, path = "Fire-Duration", defaultValue = "10_000")
    private long fireDuration;

    @ConfigInject(type = Double.class, path = "Damage", defaultValue = "6.0")
    private double damage;

    @ConfigInject(type = Double.class, path = "Distance", defaultValue = "5.0")
    private double distance;

    @ConfigInject(type = Double.class, path = "Fireball-Velocity", defaultValue = "5.0")
    private double fireballVelocity;

    @ConfigInject(type = Float.class, path = "Fireball-Yield", defaultValue = "0.0")
    private float fireballYield;

    @ConfigInject(type = Boolean.class, path = "Fireball-Incendiary", defaultValue = "true")
    private boolean fireballIncendiary;

    @ConfigInject(type = Boolean.class, path = "Friendly-Fire", defaultValue = "false")
    private boolean friendlyFire;

    public MoltenBlast(final Mage module) {
        super(module, ActiveSkillType.AXE);
    }

    @Override
    public Class<MoltenBlastData> getClassOfData() {
        return MoltenBlastData.class;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with an Axe to Activate.",
                "",
                "Shoot a large fireball that deals",
                "area of effect damage, and igniting any players hit.",
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyString(level)))
        };
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void onActivate(final Player player, final int level) {
        final MoltenBlastData data = new MoltenBlastData(player, level, this.duration);

        final LargeFireball fireball = player.launchProjectile(LargeFireball.class);

        fireball.setVelocity(fireball.getVelocity().multiply(this.fireballVelocity));

        fireball.setYield(this.fireballYield);
        fireball.setIsIncendiary(this.fireballIncendiary);

        data.setFireBall(fireball);

        this.addUser(data);

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @Override
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            final MoltenBlastData data = this.getUserByPlayer(player);
            if (data.getFireBall() != null) {
                data.getFireBall().remove();
            }
        }

        super.reset(player);
    }

    @Override
    public void onExpire(final Player player, final MoltenBlastData data) {
        new SoundCreator(Sound.NOTE_STICKS).play(player);

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You failed <green><var></green>.", Collections.singletonList(this.getDisplayName(data.getLevel())));
    }

    @Override
    public boolean isActive(final Player player) {
        final MoltenBlastData data = this.getUserByPlayer(player);
        if (data != null && data.getFireBall() != null) {
            UtilMessage.simpleMessage(player, this.getModule().getName(), "<green><var></green> is already active.", Collections.singletonList(this.getDisplayName(data.getLevel())));
            return true;
        }

        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(final EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof LargeFireball)) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        final LargeFireball fireball = UtilJava.cast(LargeFireball.class, event.getEntity());

        final MoltenBlastData data = this.getDataByFireball(fireball);
        if (data == null) {
            return;
        }

        final Player player = Bukkit.getPlayer(data.getUUID());

        int count = 0;

        for (final LivingEntity targetEntity : UtilEntity.getNearbyEntities(LivingEntity.class, event.getLocation(), this.distance)) {
            if (targetEntity instanceof Player) {
                final Player targetPlayer = UtilJava.cast(Player.class, targetEntity);

                final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, player, targetPlayer);
                UtilServer.callEvent(friendlyFireEvent);
                if (friendlyFireEvent.isCancelled()) {
                    continue;
                }

                if (!(this.friendlyFire)) {
                    if (player == targetPlayer || !(friendlyFireEvent.isVulnerable())) {
                        targetEntity.setVelocity(targetEntity.getLocation().toVector().subtract(fireball.getLocation().toVector()).normalize().multiply(2.0D).setY(0.75D));
                    }

                    if (player == targetPlayer) {
                        continue;
                    }

                    if (!(friendlyFireEvent.isVulnerable())) {
                        UtilMessage.simpleMessage(targetPlayer, this.getModule().getName(), "<var> used <green><var></green>.", Arrays.asList(friendlyFireEvent.getPlayerName(), this.getDisplayName(data.getLevel())));
                        continue;
                    }
                }

                if (targetPlayer != player) {
                    count++;

                    UtilMessage.simpleMessage(targetPlayer, this.getModule().getName(), "<var> hit you with <green><var></green>.", Arrays.asList(friendlyFireEvent.getPlayerName(), this.getDisplayName(data.getLevel())));
                }
            }

            targetEntity.setVelocity(targetEntity.getLocation().toVector().subtract(fireball.getLocation().toVector()).normalize().multiply(2.0D).setY(0.75D));

            UtilDamage.damage(targetEntity, player, EntityDamageEvent.DamageCause.CUSTOM, this.damage, this.getDisplayName(data.getLevel()), 1000L);

            targetEntity.setFireTicks((int) (this.fireDuration / 50L));
        }

        if (count > 0) {
            UtilMessage.simpleMessage(player, this.getModule().getName(), "You attacked <yellow><var></yellow> opponents with <green><var></green>.", Arrays.asList(String.valueOf(count), this.getDisplayName(data.getLevel())));
        }

        this.removeUser(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCustomPreDamage(final CustomPreDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
            return;
        }

        if (!(event.getProjectile() instanceof LargeFireball)) {
            return;
        }

        final LargeFireball fireball = event.getProjectileByClass(LargeFireball.class);

        final MoltenBlastData data = this.getDataByFireball(fireball);
        if (data == null) {
            return;
        }

        event.setCancelled(true);
    }

    private MoltenBlastData getDataByFireball(final LargeFireball fireball) {
        for (final MoltenBlastData data : this.getUsers().values()) {
            if (data.getFireBall() == null || !(data.getFireBall().equals(fireball))) {
                continue;
            }

            return data;
        }

        return null;
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

    @Override
    public void onShutdown() {
        for (final MoltenBlastData data : this.getUsers().values()) {
            final LargeFireball fireBall = data.getFireBall();
            if (fireBall == null) {
                continue;
            }

            fireBall.remove();
        }

        super.onShutdown();
    }
}