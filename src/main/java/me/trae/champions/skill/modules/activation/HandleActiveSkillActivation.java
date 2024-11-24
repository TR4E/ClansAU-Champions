package me.trae.champions.skill.modules.activation;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.skill.events.SkillActivateEvent;
import me.trae.api.champions.skill.events.SkillPreActivateEvent;
import me.trae.champions.Champions;
import me.trae.champions.build.data.RoleBuild;
import me.trae.champions.build.data.RoleSkill;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.weapon.types.ChampionsPvPWeapon;
import me.trae.core.Core;
import me.trae.core.energy.EnergyManager;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.recharge.RechargeManager;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.UtilServer;
import me.trae.core.weapon.WeaponManager;
import me.trae.core.world.events.PlayerItemInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

public class HandleActiveSkillActivation extends SpigotListener<Champions, SkillManager> {

    public HandleActiveSkillActivation(final SkillManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerItemInteract(final PlayerItemInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final ItemStack itemStack = event.getItemStack();

        if (!(this.getInstance(Core.class).getManagerByClass(WeaponManager.class).getWeaponByItemStack(itemStack) instanceof ChampionsPvPWeapon)) {
            return;
        }

        final Player player = event.getPlayer();

        final Role role = this.getInstance().getManagerByClass(RoleManager.class).getPlayerRole(player);
        if (role == null) {
            return;
        }

        final RoleBuild roleBuild = role.getRoleBuildByPlayer(player);
        if (roleBuild == null) {
            return;
        }

        final SkillType skillType = SkillType.getByMaterial(itemStack.getType());
        if (skillType == null) {
            return;
        }

        if (!(skillType.getActionType().isAction(event.getAction()))) {
            return;
        }

        final RoleSkill roleSkill = roleBuild.getRoleSkillByType(skillType);
        if (roleSkill == null) {
            return;
        }

        final ActiveSkill<?, ?> skill = UtilJava.cast(ActiveSkill.class, role.getSubModuleByName(roleSkill.getName()));
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

        if (!(this.canActivate(player, skill))) {
            return;
        }

        final SkillActivateEvent activateEvent = new SkillActivateEvent(skill, player);
        UtilServer.callEvent(activateEvent);
        if (activateEvent.isCancelled()) {
            return;
        }

        skill.onActivate(player, level);
    }

    private boolean canActivate(final Player player, final ActiveSkill<?, ?> skill) {
        if (!(skill.canActivate(player))) {
            return false;
        }

        final int level = skill.getLevel(player);

        final RechargeManager rechargeManager = this.getInstance(Core.class).getManagerByClass(RechargeManager.class);

        if (skill.hasRecharge(level)) {
            if (rechargeManager.isCooling(player, skill.getName(), true)) {
                return false;
            }
        }

        if (skill.hasEnergy(level)) {
            final EnergyManager energyManager = this.getInstance(Core.class).getManagerByClass(EnergyManager.class);

            if (!(energyManager.use(player, skill.getName(), skill.getEnergy(level), true))) {
                return false;
            }
        }

        if (skill.hasRecharge(level)) {
            if (!(rechargeManager.add(player, skill.getName(), skill.getRecharge(level), true))) {
                return false;
            }
        }

        return true;
    }
}