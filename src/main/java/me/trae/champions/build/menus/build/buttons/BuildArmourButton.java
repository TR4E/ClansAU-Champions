package me.trae.champions.build.menus.build.buttons;

import me.trae.champions.build.menus.build.BuildCustomizationMenu;
import me.trae.core.menu.Button;
import me.trae.core.utility.enums.ArmourSlotType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class BuildArmourButton extends Button<BuildCustomizationMenu> {

    public BuildArmourButton(final BuildCustomizationMenu menu, final int slot, final ItemStack itemStack) {
        super(menu, slot, itemStack);
    }

    @Override
    public String getDisplayName() {
        return String.format("<green><bold>%s %s", this.getMenu().getRole().getName(), Objects.requireNonNull(ArmourSlotType.getByMaterial(this.getBuilder().getItemStack().getType())).getName());
    }

    @Override
    public void onClick(final Player player, final ClickType clickType) {
    }
}