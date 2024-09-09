package me.trae.champions.role.commands;

import me.trae.champions.Champions;
import me.trae.champions.role.Role;
import me.trae.champions.role.RoleManager;
import me.trae.champions.role.menus.RoleSelectionMenu;
import me.trae.core.client.Client;
import me.trae.core.client.enums.Rank;
import me.trae.core.command.types.Command;
import me.trae.core.command.types.models.PlayerCommandType;
import me.trae.core.utility.UtilMenu;
import org.bukkit.entity.Player;

public class KitCommand extends Command<Champions, RoleManager> implements PlayerCommandType {

    public KitCommand(final RoleManager manager) {
        super(manager, "kit", new String[]{"role", "class"}, Rank.ADMIN);
    }

    @Override
    public void execute(final Player player, final Client client, final String[] args) {
        if (args.length == 0) {
            UtilMenu.open(new RoleSelectionMenu(this.getManager(), player) {
                @Override
                public void onClick(final Player player, final Role role) {
                    player.closeInventory();

                    this.getManager().giveKit(player, role, client.isAdministrating());
                }
            });
            return;
        }
    }
}