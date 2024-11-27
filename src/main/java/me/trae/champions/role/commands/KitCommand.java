package me.trae.champions.role.commands;

import me.trae.api.champions.role.Role;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.champions.role.menus.RoleSelectionMenu;
import me.trae.core.Core;
import me.trae.core.client.Client;
import me.trae.core.client.ClientManager;
import me.trae.core.client.enums.Rank;
import me.trae.core.command.types.Command;
import me.trae.core.command.types.models.AnyCommandType;
import me.trae.core.utility.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class KitCommand extends Command<Champions, RoleManager> implements AnyCommandType {

    public KitCommand(final RoleManager manager) {
        super(manager, "kit", new String[]{"role", "class"}, Rank.ADMIN);
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            if (!(UtilCommand.isValidSender(sender, Player.class, true))) {
                return;
            }

            final Player player = UtilJava.cast(Player.class, sender);

            UtilMenu.open(new RoleSelectionMenu(this.getManager(), player) {
                @Override
                public void onClick(final Player player, final Role role) {
                    player.closeInventory();

                    final Client client = this.getManager().getInstance(Core.class).getManagerByClass(ClientManager.class).getClientByPlayer(player);

                    this.getManager().giveKit(player, role, client.isAdministrating());
                }
            });
            return;
        }

        final Role role = this.getManager().searchRole(sender, args[0], true);
        if (role == null) {
            return;
        }

        if (args.length == 1) {
            if (!(UtilCommand.isValidSender(sender, Player.class, true))) {
                return;
            }

            final Player player = UtilJava.cast(Player.class, sender);

            final Client client = this.getManager().getInstance(Core.class).getManagerByClass(ClientManager.class).getClientByPlayer(player);

            this.getManager().giveKit(player, role, client.isAdministrating());

            UtilMessage.simpleMessage(player, "Kit", "You received <green><var> Class</green>.", Collections.singletonList(role.getName()));
            return;
        }

        if (args.length == 2) {
            final Player targetPlayer = UtilPlayer.searchPlayer(sender, args[1], true);
            if (targetPlayer == null) {
                return;
            }

            if (targetPlayer == sender) {
                this.execute(sender, new String[]{role.getName()});
                return;
            }

            final Client targetClient = this.getManager().getInstance(Core.class).getManagerByClass(ClientManager.class).getClientByPlayer(targetPlayer);

            this.getManager().giveKit(targetPlayer, role, targetClient.isAdministrating());

            UtilMessage.simpleMessage(sender, "Kit", "You gave <green><var> Class</green> to <yellow><var></yellow>.", Arrays.asList(role.getName(), targetPlayer.getName()));
            UtilMessage.simpleMessage(targetPlayer, "Kit", "<yellow><var></yellow> gave you <green><var> Class</green>.", Arrays.asList(sender.getName(), role.getName()));
        }
    }

    @Override
    public List<String> getTabCompletion(final CommandSender sender, final String[] args) {
        if (args.length == 1) {
            return this.getManager().getModulesByClass(Role.class).stream().map(Role::getName).filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}