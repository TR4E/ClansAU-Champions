package me.trae.champions.role.modules;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.role.events.RoleChangeEvent;
import me.trae.api.champions.role.events.RoleUpdaterEvent;
import me.trae.champions.Champions;
import me.trae.champions.build.BuildManager;
import me.trae.champions.role.RoleManager;
import me.trae.champions.utility.UtilRole;
import me.trae.core.Core;
import me.trae.core.client.Client;
import me.trae.core.client.ClientManager;
import me.trae.core.framework.types.frame.SpigotUpdater;
import me.trae.core.updater.annotations.Update;
import me.trae.core.utility.UtilServer;
import me.trae.core.utility.UtilTime;
import me.trae.core.utility.enums.ArmourSlotType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HandleRoleEquip extends SpigotUpdater<Champions, RoleManager> {

    public HandleRoleEquip(final RoleManager manager) {
        super(manager);
    }

    @Update(delay = 200L)
    public void onUpdater() {
        final BuildManager buildManager = this.getInstanceByClass().getManagerByClass(BuildManager.class);
        final ClientManager clientManager = this.getInstanceByClass(Core.class).getManagerByClass(ClientManager.class);

        final List<Role> roleList = this.getManager().getModulesByClass(Role.class);

        for (final Player player : UtilServer.getOnlinePlayers()) {
            if (!(buildManager.getBuilds().containsKey(player.getUniqueId()))) {
                final Client client = clientManager.getClientByPlayer(player);
                if (client != null && !(UtilTime.elapsed(client.getLastJoined(), 400L))) {
                    continue;
                }
            }

            Role playerRole = null;

            for (final Role role : roleList) {
                if (!(role.isEnabled())) {
                    continue;
                }

                if (!(role.hasArmour(player))) {
                    continue;
                }

                playerRole = role;
                break;
            }

            UtilServer.callEvent(new RoleUpdaterEvent(playerRole, player));

            this.equip(player, playerRole);
        }

        this.getManager().getPlayerRoles().keySet().removeIf(uuid -> Bukkit.getPlayer(uuid) == null);
    }

    private void equip(final Player player, final Role role) {
        final ItemStack helmetItemStack = player.getEquipment().getHelmet();
        if (helmetItemStack != null && !(ArmourSlotType.HELMET.isValid(helmetItemStack.getType()))) {
            this.getManager().removePlayerRole(player);
            return;
        }

        final Role oldRole = this.getManager().getPlayerRole(player);

        if (this.getManager().hasPlayerRole(player) && oldRole == role) {
            return;
        }

        if (oldRole != null) {
            oldRole.reset(player);
        }

        this.getManager().setPlayerRole(player, role);

        UtilRole.equipRoleEffect(role, player, false);

        UtilServer.callEvent(new RoleChangeEvent(role, player));
    }

    @Override
    public boolean isLocked() {
        return true;
    }
}