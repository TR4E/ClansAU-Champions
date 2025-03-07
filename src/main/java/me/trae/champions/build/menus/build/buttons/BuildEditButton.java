package me.trae.champions.build.menus.build.buttons;

import me.trae.api.champions.role.Role;
import me.trae.champions.build.data.RoleBuild;
import me.trae.champions.build.menus.build.BuildCustomizationMenu;
import me.trae.champions.build.menus.build.buttons.interfaces.IBuildButton;
import me.trae.champions.build.menus.skill.SkillEditMenu;
import me.trae.core.menu.Button;
import me.trae.core.utility.UtilMenu;
import me.trae.core.utility.UtilString;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public abstract class BuildEditButton extends Button<BuildCustomizationMenu> implements IBuildButton {

    public BuildEditButton(final BuildCustomizationMenu menu, final int slot) {
        super(menu, slot, new ItemStack(Material.ANVIL));
    }

    @Override
    public String getDisplayName() {
        return UtilString.format("<green><bold>Edit Custom Build #%s", this.getID());
    }

    @Override
    public String[] getLore() {
        return new String[]{
                "Edit your abilities!"
        };
    }

    @Override
    public void onClick(final Player player, final ClickType clickType) {
        if (!(Arrays.asList(ClickType.LEFT, ClickType.RIGHT).contains(clickType))) {
            return;
        }

        final Role role = this.getMenu().getRole();
        final RoleBuild roleBuild = this.getRoleBuild();

        UtilMenu.open(new SkillEditMenu(this.getMenu().getManager(), player, role, roleBuild.getID()) {
            @Override
            public Role getRole() {
                return role;
            }

            @Override
            public RoleBuild getRoleBuild() {
                return roleBuild;
            }
        });
    }
}