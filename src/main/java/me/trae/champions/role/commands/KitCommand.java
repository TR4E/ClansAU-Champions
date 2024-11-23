package me.trae.champions.role.commands;

import me.trae.champions.Champions;
import me.trae.api.champions.role.Role;
import me.trae.champions.role.RoleManager;
import me.trae.champions.role.menus.RoleSelectionMenu;
import me.trae.core.client.Client;
import me.trae.core.client.enums.Rank;
import me.trae.core.command.types.Command;
import me.trae.core.command.types.models.PlayerCommandType;
import me.trae.core.gamer.Gamer;
import me.trae.core.utility.UtilMenu;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilPlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class KitCommand extends Command<Champions, RoleManager> implements PlayerCommandType {

    public KitCommand(final RoleManager manager) {
        super(manager, "kit", new String[]{"role", "class"}, Rank.ADMIN);
    }

    @Override
    public void execute(final Player player, final Client client, final Gamer gamer, final String[] args) {
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

        final Role role = this.getManager().searchRole(player, args[0], true);
        if (role == null) {
            return;
        }

        if (args.length == 1) {
            this.getManager().giveKit(player, role, client.isAdministrating());

            UtilMessage.simpleMessage(player, "Kit", "You received <green><var> Class</green>.", Collections.singletonList(role.getName()));
            return;
        }

        if (args.length == 2) {
            final Player target = UtilPlayer.searchPlayer(player, args[1], true);
            if (target == null) {
                return;
            }

            if (target == player) {
                this.execute(player, client, gamer, new String[]{role.getName()});
                return;
            }

            this.getManager().giveKit(target, role, client.isAdministrating());

            UtilMessage.simpleMessage(player, "Kit", "You gave <green><var> Class</green> to <yellow><var></yellow>.", Arrays.asList(role.getName(), target.getName()));
        }
    }

    @Override
    public List<String> getTabCompletion(final Player player, final Client client, final Gamer gamer, final String[] args) {
        if (args.length == 1) {
            return this.getManager().getModulesByClass(Role.class).stream().map(Role::getName).filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}