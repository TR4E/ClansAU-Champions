package me.trae.champions.build.menus.skill.buttons;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.skill.Skill;
import me.trae.champions.build.data.RoleBuild;
import me.trae.champions.build.data.RoleSkill;
import me.trae.champions.build.enums.BuildProperty;
import me.trae.champions.build.menus.skill.SkillEditMenu;
import me.trae.champions.build.menus.skill.buttons.interfaces.ISkillSelectButton;
import me.trae.core.menu.Button;
import me.trae.core.utility.UtilMessage;
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

        final String strikeLine = UtilMessage.strikeLine(lore);

        if (!(lore.isEmpty())) {
            lore.add(" ");

            lore.add(strikeLine);
        }

        lore.add(UtilString.pair("<yellow>Skill Token Cost", String.format("<white>%s", this.getSkill().getTokenCost())));
        lore.add(UtilString.pair("<yellow>Skill Max Level", String.format("<white>%s", this.getSkill().getMaxLevel())));

        lore.add(" ");

        if (this.getSkill().getMaxLevel() == level) {
            lore.add("<gold><bold>You have reached the max skill level!");
        } else if (this.isSkillTypeEquippedByOtherSkill()) {
            lore.add("<gold><bold>You already have a skill of this type selected!");
        } else {
            final int skillPoints = this.getMenu().getManager().getSkillPoints(this.getMenu().getRole(), this.getMenu().getRoleBuild());
            if (skillPoints <= 0) {
                lore.add("<gold><bold>You have no skill points left!");
            } else if (this.getRoleSkill() != null) {
                lore.add("<green><bold>Left-Click to increase the skill level!");
                lore.add("<red><bold>Right-Click to decrease the skill level!");
            } else {
                lore.add("<green><bold>Left-Click to equip the skill!");
            }
        }


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
                this.onRightClickButton(player, role, roleBuild, skill, roleSkill);
                break;
        }
    }

    private void onLeftClickButton(final Player player, final Role role, final RoleBuild roleBuild, final Skill<?, ?> skill, final RoleSkill roleSkill) {
        final int skillPoints = this.getMenu().getManager().getSkillPoints(role, roleBuild);
        if (skillPoints <= 0 || skillPoints < skill.getTokenCost()) {
            new SoundCreator(Sound.ITEM_BREAK, 1.0F, 2.0F).play(player);
            return;
        }

        if (this.isSkillTypeEquippedByOtherSkill()) {
            new SoundCreator(Sound.ITEM_BREAK, 1.0F, 2.0F).play(player);
            return;
        }

        if (roleSkill != null) {
            if (roleSkill.getLevel() >= skill.getMaxLevel()) {
                new SoundCreator(Sound.ITEM_BREAK, 1.0F, 2.0F).play(player);
                return;
            }

            roleSkill.setLevel(roleSkill.getLevel() + 1);
        } else {
            final RoleSkill newRoleSkill = new RoleSkill(skill, 1);
            roleBuild.addSkill(newRoleSkill);
        }

        skill.reset(player);
        skill.removeUser(player);

        if (!(this.getMenu().getManager().isBuildByID(player, role, roleBuild.getID()))) {
            this.getMenu().getManager().addBuild(roleBuild);
            this.getMenu().getManager().getRepository().saveData(roleBuild);
            role.reset(player);
        } else {
            this.getMenu().getManager().getRepository().updateData(roleBuild, BuildProperty.SKILLS);
        }

        this.getMenu().setUpdatedRole(role);

        this.getMenu().getManager().setActiveRoleBuild(player, role, roleBuild);

        new SoundCreator(Sound.ORB_PICKUP, 1.0F, 1.0F).play(player);

        this.getMenu().build();
    }

    private void onRightClickButton(final Player player, final Role role, final RoleBuild roleBuild, final Skill<?, ?> skill, final RoleSkill roleSkill) {
        if (this.isSkillTypeEquippedByOtherSkill()) {
            new SoundCreator(Sound.ITEM_BREAK).play(player);
            return;
        }

        if (roleSkill == null) {
            new SoundCreator(Sound.ITEM_BREAK).play(player);
            return;
        }

        if (roleSkill.getLevel() > 1) {
            roleSkill.setLevel(roleSkill.getLevel() - 1);
        } else {
            roleBuild.removeSkill(roleSkill);
        }

        skill.reset(player);
        skill.removeUser(player);

        if (roleBuild.getSkills().isEmpty()) {
            this.getMenu().getManager().removeBuild(roleBuild);
            this.getMenu().getManager().getRepository().deleteData(roleBuild);
            role.reset(player);
        } else {
            this.getMenu().getManager().getRepository().updateData(roleBuild, BuildProperty.SKILLS);
        }

        this.getMenu().setUpdatedRole(role);

        new SoundCreator(Sound.ORB_PICKUP, 2.0F, 2.0F).play(player);

        this.getMenu().build();
    }

    private boolean isSkillTypeEquippedByOtherSkill() {
        for (final RoleSkill roleSkill : this.getMenu().getRoleBuild().getSkills().values()) {
            if (this.getRoleSkill() != null && this.getRoleSkill().equals(roleSkill)) {
                continue;
            }

            if (!(roleSkill.getType().equals(this.getSkill().getType()))) {
                continue;
            }

            if (roleSkill.getLevel() <= 0) {
                continue;
            }

            return true;
        }

        return false;
    }
}