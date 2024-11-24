package me.trae.champions.build.commands;

import me.trae.champions.Champions;
import me.trae.champions.build.BuildManager;
import me.trae.core.client.Client;
import me.trae.core.client.enums.Rank;
import me.trae.core.command.types.Command;
import me.trae.core.command.types.models.PlayerCommandType;
import me.trae.core.gamer.Gamer;
import org.bukkit.entity.Player;

public class BuildCommand extends Command<Champions, BuildManager> implements PlayerCommandType {

    public BuildCommand(final BuildManager manager) {
        super(manager, "build", new String[0], Rank.OWNER);
    }

    @Override
    public void execute(final Player player, final Client client, final Gamer gamer, final String[] args) {
        this.getManager().openMenu(player);
    }
}