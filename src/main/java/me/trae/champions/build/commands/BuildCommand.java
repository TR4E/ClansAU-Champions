package me.trae.champions.build.commands;

import me.trae.champions.Champions;
import me.trae.champions.build.BuildManager;
import me.trae.champions.build.data.RoleBuild;
import me.trae.champions.build.data.RoleSkill;
import me.trae.champions.role.Role;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.enums.SkillType;
import me.trae.core.client.Client;
import me.trae.core.client.enums.Rank;
import me.trae.core.command.types.Command;
import me.trae.core.command.types.models.PlayerCommandType;
import me.trae.core.gamer.Gamer;
import me.trae.core.utility.UtilMessage;
import org.bukkit.entity.Player;

import java.util.Collections;

public class BuildCommand extends Command<Champions, BuildManager> implements PlayerCommandType {

    public BuildCommand(final BuildManager manager) {
        super(manager, "build", new String[0], Rank.OWNER);
    }

    @Override
    public void execute(final Player player, final Client client, final Gamer gamer, final String[] args) {
        final Role role = this.getInstance().getManagerByClass(RoleManager.class).getPlayerRole(player);
        if (role == null) {
            return;
        }

        RoleBuild roleBuild = this.getManager().getBuildByRole(player, role, 0);
        if (roleBuild == null) {
//            roleBuild = new RoleBuild(player, role, 0, false);
//
//            roleBuild.addSkill(new RoleSkill(SkillType.SWORD, "Riposte", 1));
//            roleBuild.addSkill(new RoleSkill(SkillType.AXE, "Bulls Charge", 1));
//            roleBuild.addSkill(new RoleSkill(SkillType.PASSIVE_A, "Swordsmanship", 1));
//            roleBuild.addSkill(new RoleSkill(SkillType.PASSIVE_B, "Thorns", 1));
//            roleBuild.addSkill(new RoleSkill(SkillType.PASSIVE_B, "Swim", 1));
//
//            getManager().addBuild(roleBuild);
//            getManager().getRepository().saveData(roleBuild);
            return;
        }

        UtilMessage.simpleMessage(player, "Build", "<var> Skills:", Collections.singletonList(role.getName()));
        UtilMessage.simpleMessage(player, roleBuild.getEquipMessage());
    }
}