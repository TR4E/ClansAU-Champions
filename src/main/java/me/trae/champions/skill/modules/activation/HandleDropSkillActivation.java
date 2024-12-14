package me.trae.champions.skill.modules.activation;

import me.trae.api.champions.skill.events.SkillActivateEvent;
import me.trae.api.champions.skill.events.SkillPreActivateEvent;
import me.trae.champions.Champions;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.components.energy.EnergySkillComponent;
import me.trae.champions.skill.components.recharge.RechargeSkillComponent;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.ChannelSkill;
import me.trae.champions.skill.types.DropSkill;
import me.trae.champions.skill.types.ToggleUpdaterDropSkill;
import me.trae.champions.skill.types.interfaces.IActiveSkill;
import me.trae.champions.skill.types.models.ToggleSkill;
import me.trae.champions.weapon.models.PassiveActivatorWeapon;
import me.trae.core.Core;
import me.trae.core.energy.EnergyManager;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.recharge.RechargeManager;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.UtilServer;
import me.trae.core.utility.components.SelfManagedAbilityComponent;
import me.trae.core.weapon.WeaponManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class HandleDropSkillActivation extends SpigotListener<Champions, SkillManager> {

    public HandleDropSkillActivation(final SkillManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getItemDrop() == null) {
            return;
        }

        final ItemStack itemStack = event.getItemDrop().getItemStack();

        if (!(this.getInstance(Core.class).getManagerByClass(WeaponManager.class).getWeaponByItemStack(itemStack) instanceof PassiveActivatorWeapon)) {
            return;
        }

        final Player player = event.getPlayer();

        final DropSkill<?, ?> skill = this.getManager().getSkillByType(DropSkill.class, player, SkillType.PASSIVE_B);
        if (skill == null) {
            return;
        }

        event.setCancelled(true);

        final int level = skill.getLevel(player);
        if (level == 0) {
            return;
        }

        final SkillPreActivateEvent preActivateEvent = new SkillPreActivateEvent(skill, player);
        UtilServer.callEvent(preActivateEvent);
        if (preActivateEvent.isCancelled()) {
            return;
        }

        if (!(this.canActivateSkill(player, skill))) {
            return;
        }

        final SkillActivateEvent activateEvent = new SkillActivateEvent(skill, player);
        UtilServer.callEvent(activateEvent);
        if (activateEvent.isCancelled()) {
            return;
        }

        skill.onActivate(player, level);
    }

    private boolean canActivateSkill(final Player player, final DropSkill<?, ?> skill) {
        final int level = skill.getLevel(player);
        if (level == 0) {
            return false;
        }

        if (!(skill.canActivate(player))) {
            return false;
        }

        if (skill instanceof ToggleSkill<?> && skill.isUserByPlayer(player)) {
            final ToggleSkill<?> toggleSkill = UtilJava.cast(ToggleSkill.class, skill);

            final SkillData data = skill.getUserByPlayer(player);

            toggleSkill.onDeActivate(player, UtilJava.matchlessObjectCast(skill.getClassOfData(), data));

            skill.reset(player);
            skill.removeUser(player);
            return false;
        }

        if (!(skill instanceof SelfManagedAbilityComponent)) {
            final RechargeManager rechargeManager = this.getInstance(Core.class).getManagerByClass(RechargeManager.class);

            final RechargeSkillComponent rechargeSkillComponent = UtilJava.cast(RechargeSkillComponent.class, skill);

            if (rechargeSkillComponent.hasRecharge(level)) {
                if (rechargeManager.isCooling(player, skill.getName(), true)) {
                    return false;
                }
            }

            if (UtilJava.cast(IActiveSkill.class, skill).isActive(player)) {
                return false;
            }

            final EnergySkillComponent energySkillComponent = UtilJava.cast(EnergySkillComponent.class, skill);

            if (energySkillComponent.hasEnergy(level)) {
                final EnergyManager energyManager = this.getInstance(Core.class).getManagerByClass(EnergyManager.class);

                if (!(energyManager.use(player, skill.getName(), energySkillComponent.getEnergy(level), true))) {
                    return false;
                }
            }

            if (!(skill instanceof ToggleUpdaterDropSkill<?, ?>)) {
                if (rechargeSkillComponent.hasRecharge(level)) {
                    if (!(rechargeManager.add(player, skill.getName(), rechargeSkillComponent.getRecharge(level), true))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}