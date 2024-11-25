package me.trae.champions.build.menus.skill.buttons;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.skill.Skill;
import me.trae.champions.build.data.RoleBuild;
import me.trae.champions.build.data.RoleSkill;
import me.trae.champions.build.enums.BuildProperty;
import me.trae.champions.build.menus.skill.SkillEditMenu;
import me.trae.champions.build.menus.skill.buttons.interfaces.ISkillSelectButton;
import me.trae.core.menu.Button;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SkillSelectButton extends Button<SkillEditMenu> implements ISkillSelectButton {

    public SkillSelectButton(final SkillEditMenu menu, final int slot) {
        super(menu, slot, new ItemStack(Material.BOOK));

        if (this.getRoleSkill() != null) {
            this.getBuilder().getItemStack().setAmount(this.getRoleSkill().getLevel());
            this.getBuilder().setGlowing(true);
        }
    }

    @Override
    public String getDisplayName() {
        final RoleSkill roleSkill = this.getRoleSkill();
        final Skill<?, ?> skill = this.getSkill();

        String displayName = skill.getName();

        if (roleSkill != null) {
            displayName = String.format("<green><bold>%s (%s/%s)", displayName, roleSkill.getLevel(), skill.getMaxLevel());
        } else {
            displayName = String.format("<red>%s", displayName);
        }

        return displayName;
    }

    @Override
    public String[] getLore() {
        final int level = this.getRoleSkill() != null ? this.getRoleSkill().getLevel() : 1;

        final List<String> lore = new ArrayList<>(Arrays.asList(this.getSkill().getDescription(level)));

        for (int i = 0; i < 2; i++) {
            lore.add(" ");
        }

        lore.add(UtilString.pair("<yellow>Skill Token Cost", String.format("<white>%s", this.getSkill().getTokenCost())));

        return lore.toArray(new String[0]);
    }

    @Override
    public void onClick(final Player player, final ClickType clickType) {
        if (!(Arrays.asList(ClickType.LEFT, ClickType.RIGHT).contains(clickType))) {
            return;
        }

        final Role role = this.getMenu().getRole();
        final RoleBuild roleBuild = this.getMenu().getRoleBuild();
        final Skill<?, ?> skill = this.getSkill();
        final RoleSkill roleSkill = this.getRoleSkill();

        switch (clickType) {
            case LEFT:
                this.onLeftClickButton(player, role, roleBuild, skill, roleSkill);
                break;
            case RIGHT:
                this.onRightClickButton(player, roleBuild, roleSkill);
                break;
        }
    }

    private void onLeftClickButton(final Player player, final Role role, final RoleBuild roleBuild, final Skill<?, ?> skill, final RoleSkill roleSkill) {
        final int skillPoints = this.getMenu().getManager().getSkillPoints(role, roleBuild);
        if (skillPoints <= 0 || skillPoints < skill.getTokenCost()) {
            return;
        }

        if (roleSkill != null) {
            if (roleSkill.getLevel() >= skill.getMaxLevel()) {
                new SoundCreator(Sound.ITEM_BREAK, 1.0F, 2.0F).play(player);
                return;
            }

            roleSkill.setLevel(roleSkill.getLevel() + 1);
        } else {
            roleBuild.addSkill(new RoleSkill(skill, 1));
        }

        if (!(this.getMenu().getManager().isBuildByID(player, role, roleBuild.getID()))) {
            this.getMenu().getManager().addBuild(roleBuild);
            this.getMenu().getManager().getRepository().saveData(roleBuild);
        } else {
            this.getMenu().getManager().getRepository().updateData(roleBuild, BuildProperty.SKILLS);
        }

        this.getMenu().setUpdatedRole(role);

        this.getMenu().getManager().setActiveRoleBuild(player, role, roleBuild);

        new SoundCreator(Sound.ORB_PICKUP, 1.0F, 1.0F).play(player);

        this.getMenu().construct();
    }

    private void onRightClickButton(final Player player, final RoleBuild roleBuild, final RoleSkill roleSkill) {
        if (roleSkill == null) {
            new SoundCreator(Sound.ITEM_BREAK).play(player);
            return;
        }

        if (roleSkill.getLevel() > 1) {
            roleSkill.setLevel(roleSkill.getLevel() - 1);
        } else {
            roleBuild.removeSkill(roleSkill);
        }

        if (roleBuild.getSkills().isEmpty()) {
            this.getMenu().getManager().removeBuild(roleBuild);
            this.getMenu().getManager().getRepository().deleteData(roleBuild);
        } else {
            this.getMenu().getManager().getRepository().updateData(roleBuild, BuildProperty.SKILLS);
        }

        new SoundCreator(Sound.ORB_PICKUP, 2.0F, 2.0F).play(player);

        this.getMenu().construct();
    }
}