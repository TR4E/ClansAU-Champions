package me.trae.champions.build.menus.build.buttons;

import me.trae.champions.build.data.RoleBuild;
import me.trae.champions.build.menus.build.BuildCustomizationMenu;
import me.trae.champions.build.menus.build.buttons.interfaces.IBuildButton;
import me.trae.core.menu.Button;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public abstract class BuildEquipButton extends Button<BuildCustomizationMenu> implements IBuildButton {

    public BuildEquipButton(final BuildCustomizationMenu menu, final int slot, final int id) {
        super(menu, slot, new ItemStack(Material.INK_SACK));

        if (this.getRoleBuild() != null) {
            this.getBuilder().getItemStack().setDurability(this.getItemStackDataByID(id));
            this.getBuilder().setGlowing(this.getRoleBuild().isActive());
        }
    }

    @Override
    public String getDisplayName() {
        String displayName = "<gray>Unsaved Build";

        final RoleBuild roleBuild = this.getRoleBuild();

        if (roleBuild != null) {
            if (roleBuild.getID() == 0) {
                displayName = "<dark_green><bold>Apply Default Build";
            } else {
                displayName = UtilString.format("<yellow><bold>Apply Custom Build #%s", roleBuild.getID());
            }
        }

        return displayName;
    }

    @Override
    public String[] getLore() {
        if (this.getRoleBuild() == null) {
            return new String[0];
        }

        return this.getRoleBuild().getEquipMessage().toArray(new String[0]);
    }

    @Override
    public void onClick(final Player player, final ClickType clickType) {
        if (!(Arrays.asList(ClickType.LEFT, ClickType.RIGHT).contains(clickType))) {
            return;
        }

        final RoleBuild roleBuild = this.getRoleBuild();
        if (roleBuild == null) {
            return;
        }

        if (roleBuild.isActive()) {
            return;
        }

        this.getMenu().setUpdatedRole(this.getMenu().getRole());

        this.getMenu().getManager().setActiveRoleBuild(player, this.getMenu().getRole(), roleBuild);

        new SoundCreator(Sound.LEVEL_UP).play(player);

        this.getMenu().build();
    }

    private short getItemStackDataByID(final int id) {
        switch (id) {
            case 0:
                return 1;
            case 1:
                return 14;
            case 2:
                return 11;
            case 3:
                return 2;
            case 4:
                return 4;
        }

        return 0;
    }
}