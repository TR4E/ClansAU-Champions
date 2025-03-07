package me.trae.champions.build.menus.skill;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.skill.Skill;
import me.trae.champions.Champions;
import me.trae.champions.build.BuildManager;
import me.trae.champions.build.data.RoleSkill;
import me.trae.champions.build.menus.skill.buttons.SkillPointsButton;
import me.trae.champions.build.menus.skill.buttons.SkillSelectButton;
import me.trae.champions.build.menus.skill.buttons.SkillTypeButton;
import me.trae.champions.build.menus.skill.interfaces.ISkillMenu;
import me.trae.champions.skill.enums.SkillType;
import me.trae.core.menu.Menu;
import me.trae.core.utility.UtilColor;
import me.trae.core.utility.UtilString;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class SkillEditMenu extends Menu<Champions, BuildManager> implements ISkillMenu {

    private Role updatedRole;

    public SkillEditMenu(final BuildManager manager, final Player player, final Role role, final int id) {
        super(manager, player, 54, UtilColor.bold(ChatColor.GREEN) + UtilString.format("%s Skill Page #%s", role.getName(), id));
    }

    @Override
    public Role getUpdatedRole() {
        return this.updatedRole;
    }

    @Override
    public void setUpdatedRole(final Role updatedRole) {
        this.updatedRole = updatedRole;
    }

    @Override
    public void fillPage(final Player player) {
        addButton(new SkillPointsButton(this));

        this.addSkillTypeButtons();
        this.addSkillButtons();
    }

    private void addSkillTypeButtons() {
        int slot = 0;

        for (final SkillType skillType : SkillType.values()) {
            addButton(new SkillTypeButton(this, slot, skillType.getDisplayItemStack()) {
                @Override
                public SkillType getSkillType() {
                    return skillType;
                }
            });

            slot += 9;
        }
    }

    private void addSkillButtons() {
        int index = 1;
        int slot = 1;

        for (final SkillType skillType : SkillType.values()) {
            for (final Skill<?, ?> skill : this.getRole().getSkillsByType(Skill.class, skillType)) {
                final RoleSkill roleSkill = this.getRoleBuild().getRoleSkillByName(skill.getName());

                addButton(new SkillSelectButton(this, slot) {
                    @Override
                    public Skill<?, ?> getSkill() {
                        return skill;
                    }

                    @Override
                    public RoleSkill getRoleSkill() {
                        return roleSkill;
                    }
                });

                slot++;
            }

            index += 9;
            slot = index;
        }
    }
}