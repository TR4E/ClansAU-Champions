package me.trae.champions.skill.modules.activation;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.Champions;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.PassiveBowSkill;
import me.trae.champions.skill.types.data.BowSkillData;
import me.trae.champions.weapon.types.ChampionsPvPWeapon;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.injectors.annotations.Inject;
import me.trae.core.weapon.WeaponManager;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class HandlePassiveBowSkillActivation extends SpigotListener<Champions, SkillManager> {

    @Inject
    private WeaponManager weaponManager;

    public HandlePassiveBowSkillActivation(final SkillManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityShootBow(final EntityShootBowEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getProjectile() instanceof Arrow)) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final ItemStack itemStack = event.getBow();

        if (!(this.weaponManager.getWeaponByItemStack(itemStack) instanceof ChampionsPvPWeapon)) {
            return;
        }

        final Player player = UtilJava.cast(Player.class, event.getEntity());

        for (final SkillType skillType : Arrays.asList(SkillType.PASSIVE_A, SkillType.PASSIVE_B)) {
            final PassiveBowSkill<?, ?> skill = this.getManager().getSkillByType(PassiveBowSkill.class, player, skillType);
            if (skill == null) {
                continue;
            }

            if (skill.requireFullBowPullRange() && event.getForce() != 1.0F) {
                continue;
            }

            final int level = skill.getLevel(player);
            if (level == 0) {
                continue;
            }

            if (skill.resetDataOnShoot() || !(skill.isUserByPlayer(player))) {
                skill.onActivate(player, level);
            }

            final BowSkillData data = skill.getUserByPlayer(player);

            data.setArrow(UtilJava.cast(Arrow.class, event.getProjectile()));

            skill.onFire(player, UtilJava.matchlessObjectCast(skill.getClassOfData(), data));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHit(final ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow)) {
            return;
        }

        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Arrow arrow = UtilJava.cast(Arrow.class, event.getEntity());

        final Player player = UtilJava.cast(Player.class, arrow.getShooter());

        for (final SkillType skillType : Arrays.asList(SkillType.PASSIVE_A, SkillType.PASSIVE_B)) {
            final PassiveBowSkill<?, ?> skill = this.getManager().getSkillByType(PassiveBowSkill.class, player, skillType);
            if (skill == null) {
                continue;
            }

            final BowSkillData data = skill.getUserByPlayer(player);
            if (data == null) {
                continue;
            }

            if (!(data.hasArrow())) {
                continue;
            }

            if (!(data.isArrow(arrow))) {
                continue;
            }

            skill.onHitByLocation(player, arrow.getLocation(), UtilJava.matchlessObjectCast(skill.getClassOfData(), data));
        }

        arrow.remove();
    }

    @EventHandler
    public void onCustomPostDamage(final CustomPostDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
            return;
        }

        if (!(event.getProjectile() instanceof Arrow)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        final Player damager = event.getDamagerByClass(Player.class);

        for (final SkillType skillType : Arrays.asList(SkillType.PASSIVE_A, SkillType.PASSIVE_B)) {
            final PassiveBowSkill<?, ?> skill = this.getManager().getSkillByType(PassiveBowSkill.class, damager, skillType);
            if (skill == null) {
                continue;
            }

            final BowSkillData data = skill.getUserByPlayer(damager);
            if (data == null) {
                continue;
            }

            if (!(data.hasArrow())) {
                continue;
            }

            if (!(data.isArrow(event.getProjectileByClass(Arrow.class)))) {
                continue;
            }

            skill.onHitByEntity(damager, event.getDamagee(), event, UtilJava.matchlessObjectCast(skill.getClassOfData(), data));

            if (skill.resetDataOnShoot()) {
                skill.reset(damager);
                skill.removeUser(damager);
            }
        }
    }
}