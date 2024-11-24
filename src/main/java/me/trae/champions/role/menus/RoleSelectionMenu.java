package me.trae.champions.role.menus;

import me.trae.api.champions.role.Role;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.champions.role.menus.interfaces.IRoleSelectionMenu;
import me.trae.core.menu.Button;
import me.trae.core.menu.Menu;
import me.trae.core.utility.UtilColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class RoleSelectionMenu extends Menu<Champions, RoleManager> implements IRoleSelectionMenu {

    public RoleSelectionMenu(final RoleManager manager, final Player player) {
        super(manager, player, 36, "Select a Class");
    }

    @Override
    public void fillPage(final Player player) {
        int slot = 0;

        for (final Role role : this.getManager().getModulesByClass(Role.class)) {
            for (final Material material : role.getArmour()) {
                addButton(new Button<RoleSelectionMenu>(this, slot, new ItemStack(material)) {
                    @Override
                    public String getDisplayName() {
                        return UtilColor.bold(ChatColor.GREEN) + String.format("%s Class", role.getName());
                    }

                    @Override
                    public String[] getLore() {
                        return role.getDescription();
                    }

                    @Override
                    public void onClick(final Player player, final ClickType clickType) {
                        this.getMenu().onClick(player, role);
                    }
                });

                slot += 9;
            }

            slot -= 34;
        }
    }
}