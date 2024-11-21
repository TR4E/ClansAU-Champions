package me.trae.champions.role.modules;

import me.trae.champions.Champions;
import me.trae.champions.role.Role;
import me.trae.champions.role.RoleManager;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.framework.types.frame.SpigotListener;
import me.trae.core.item.ItemBuilder;
import me.trae.core.item.events.ItemUpdateEvent;
import me.trae.core.utility.UtilString;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

import java.util.Arrays;

public class HandleChampionsItemBuilderUpdate extends SpigotListener<Champions, RoleManager> {

    @ConfigInject(type = String.class, path = "Display-Name-ChatColor", defaultValue = "GOLD")
    private String displayNameChatColor;

    public HandleChampionsItemBuilderUpdate(final RoleManager manager) {
        super(manager);
    }

    @EventHandler
    public void onItemUpdate(final ItemUpdateEvent event) {
        final ItemBuilder builder = event.getBuilder();

        final Material material = builder.getItemStack().getType();

        for (final Role role : this.getManager().getModulesByClass(Role.class)) {
            if (!(role.getArmour().contains(material))) {
                continue;
            }

            builder.setDisplayName(ChatColor.valueOf(this.displayNameChatColor) + String.format("%s %s", role.getName(), UtilString.clean(material.name().split("_")[1])));
            builder.setLore(Arrays.asList(role.getDescription()));
            break;
        }
    }
}