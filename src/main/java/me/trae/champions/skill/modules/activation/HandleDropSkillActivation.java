package me.trae.champions.skill.modules.activation;

import me.trae.api.champions.skill.events.SkillActivateEvent;
import me.trae.api.champions.skill.events.SkillPreActivateEvent;
import me.trae.champions.Champions;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.components.energy.EnergySkillComponent;
import me.trae.champions.skill.components.recharge.RechargeSkillComponent;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.DropSkill;
import me.trae.champions.skill.types.ToggleUpdaterDropSkill;
import me.trae.champions.skill.types.interfaces.IActiveSkill;
import me.trae.champions.skill.types.models.ToggleSkill;
import me.trae.champions.weapon.models.PassiveActivatorWeapon;
import me.trae.core.energy.EnergyManager;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.recharge.RechargeManager;
import me.trae.core.utility.*;
import me.trae.core.utility.components.SelfManagedAbilityComponent;
import me.trae.core.utility.injectors.annotations.Inject;
import me.trae.core.weapon.WeaponManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class HandleDropSkillActivation extends SpigotListener<Champions, SkillManager> {

    @Inject
    private WeaponManager weaponManager;

    @Inject
    private RechargeManager rechargeManager;

    @Inject
    private EnergyManager energyManager;

    public HandleDropSkillActivation(final SkillManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getItemDrop() == null) {
            return;
        }

        final Player player = event.getPlayer();

        if (UtilInventory.isInventoryTracker(player)) {
            return;
        }

        final ItemStack itemStack = event.getItemDrop().getItemStack();

        if (!(this.weaponManager.getWeaponByItemStack(itemStack) instanceof PassiveActivatorWeapon)) {
            return;
        }

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

        UtilLogger.log(Champions.class, "Skills", "Activations", UtilString.format("%s used %s", player.getName(), skill.getDisplayName(level)));
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
            final RechargeSkillComponent rechargeSkillComponent = UtilJava.cast(RechargeSkillComponent.class, skill);

            if (rechargeSkillComponent.hasRecharge(level)) {
                if (this.rechargeManager.isCooling(player, skill.getName(), true)) {
                    return false;
                }
            }

            if (UtilJava.cast(IActiveSkill.class, skill).isActive(player)) {
                return false;
            }

            final EnergySkillComponent energySkillComponent = UtilJava.cast(EnergySkillComponent.class, skill);

            if (energySkillComponent.hasEnergy(level)) {
                if (!(this.energyManager.use(player, skill.getName(), energySkillComponent.getEnergy(level), true))) {
                    return false;
                }
            }

            if (!(skill instanceof ToggleUpdaterDropSkill<?, ?>)) {
                if (rechargeSkillComponent.hasRecharge(level)) {
                    if (!(this.rechargeManager.add(player, skill.getName(), rechargeSkillComponent.getRecharge(level), true))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}