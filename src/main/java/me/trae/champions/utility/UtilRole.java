package me.trae.champions.utility;

import me.trae.api.champions.role.Role;
import me.trae.champions.build.data.RoleBuild;
import me.trae.core.utility.UtilMessage;
import org.bukkit.entity.Player;

import java.util.Collections;

public class UtilRole {

    public static void equipRoleEffect(final Role role, final Player player, final boolean updatedFromBuildMenu) {
        if (role == null) {
            me.trae.api.champions.utility.UtilRole.playEffect(player, "None", false);
        } else {
            me.trae.api.champions.utility.UtilRole.playEffect(player, role.getName(), true);

            final RoleBuild roleBuild = role.getRoleBuildByPlayer(player);

            if (updatedFromBuildMenu) {
                UtilMessage.simpleMessage(player, "Build", "You have equipped <white><var></white>.", Collections.singletonList(roleBuild.getDisplayName()));
                UtilMessage.message(player, "Skills", "Listing Skills:");
            } else {
                UtilMessage.simpleMessage(player, "Skills", "Listing <white><var></white> Skills:", Collections.singletonList(roleBuild.getDisplayName()));
            }

            UtilMessage.simpleMessage(player, roleBuild.getEquipMessage());
        }
    }
}
