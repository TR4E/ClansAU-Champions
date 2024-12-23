package me.trae.champions.build.commands;

import me.trae.api.champions.role.Role;
import me.trae.champions.Champions;
import me.trae.champions.build.BuildManager;
import me.trae.champions.build.data.RoleBuild;
import me.trae.champions.role.RoleManager;
import me.trae.core.client.Client;
import me.trae.core.client.enums.Rank;
import me.trae.core.command.types.Command;
import me.trae.core.command.types.models.PlayerCommandType;
import me.trae.core.gamer.Gamer;
import me.trae.core.utility.UtilMessage;
import org.bukkit.entity.Player;

import java.util.Collections;

public class CheckBuildCommand extends Command<Champions, BuildManager> implements PlayerCommandType {

    public CheckBuildCommand(final BuildManager manager) {
        super(manager, "checkbuild", new String[]{"viewbuild", "skills"}, Rank.DEFAULT);
    }

    @Override
    public void execute(final Player player, final Client client, final Gamer gamer, final String[] args) {
        if (args.length == 0) {
            final Role role = this.getInstance().getManagerByClass(RoleManager.class).getPlayerRole(player);
            if (role == null) {
                UtilMessage.message(player, "Check Build", "You are not wearing a class!");
                return;
            }

            final RoleBuild roleBuild = role.getRoleBuildByPlayer(player);

            UtilMessage.simpleMessage(player, "Skills", "Listing <white><var></white> Skills:", Collections.singletonList(roleBuild.getDisplayName()));
            UtilMessage.simpleMessage(player, roleBuild.getEquipMessage());
        }
    }
}