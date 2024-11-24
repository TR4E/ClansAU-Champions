package me.trae.champions.build.menus.skill.buttons;

import me.trae.champions.build.menus.skill.SkillEditMenu;
import me.trae.champions.build.menus.skill.buttons.interfaces.ISkillTypeButton;
import me.trae.core.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class SkillTypeButton extends Button<SkillEditMenu> implements ISkillTypeButton {

    public SkillTypeButton(final SkillEditMenu menu, final int slot, final ItemStack itemStack) {
        super(menu, slot, itemStack);
    }

    @Override
    public String getDisplayName() {
        return String.format("<green><bold>%s Skills", this.getSkillType().getName());
    }

    @Override
    public void onClick(final Player player, final ClickType clickType) {
    }
}