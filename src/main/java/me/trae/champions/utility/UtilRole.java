package me.trae.champions.utility;

import me.trae.api.champions.role.Role;
import me.trae.champions.build.data.RoleBuild;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collections;

public class UtilRole {

    public static void equipRoleEffect(final Role role, final Player player, final boolean equipped, final boolean updatedFromBuildMenu) {
        if (role == null) {
            if (equipped) {
                new SoundCreator(Sound.HORSE_ARMOR, 5.0F, 5.09F).play(player.getLocation());

                UtilMessage.simpleMessage(player, "Class", UtilString.pair("Armor Class", "<red>None"));
            }
        } else {
            new SoundCreator(Sound.HORSE_ARMOR, 2.0F, 1.09F).play(player.getLocation());

            if (equipped) {
                UtilMessage.simpleMessage(player, "Class", UtilString.pair("Armor Class", String.format("<green>%s", role.getName())));
            }

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
