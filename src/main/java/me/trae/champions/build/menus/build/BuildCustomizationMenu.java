package me.trae.champions.build.menus.build;

import me.trae.api.champions.role.Role;
import me.trae.champions.Champions;
import me.trae.champions.build.BuildManager;
import me.trae.champions.build.data.RoleBuild;
import me.trae.champions.build.data.types.DefaultRoleBuild;
import me.trae.champions.build.menus.build.buttons.BuildArmourButton;
import me.trae.champions.build.menus.build.buttons.BuildDeleteButton;
import me.trae.champions.build.menus.build.buttons.BuildEditButton;
import me.trae.champions.build.menus.build.buttons.BuildEquipButton;
import me.trae.champions.build.menus.build.interfaces.IBuildMenu;
import me.trae.core.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public abstract class BuildCustomizationMenu extends Menu<Champions, BuildManager> implements IBuildMenu {

    private Role updatedRole;

    public BuildCustomizationMenu(final BuildManager manager, final Player player, final Role role) {
        super(manager, player, 45, String.format("%s Class Editor", role.getName()));
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
        this.addArmourButtons(Arrays.asList(9, 18, 27, 36));

        for (int i = 0; i < 5; i++) {
            final RoleBuild roleBuild = this.getManager().getBuildByID(player, this.getRole(), i);

            this.addEquipButtons(Arrays.asList(0, 2, 4, 6, 8).get(i), i, roleBuild);

            if (i > 0) {
                this.addEditButtons(Arrays.asList(20, 22, 24, 26).get(i - 1), i, roleBuild);
                this.addDeleteButtons(Arrays.asList(38, 40, 42, 44).get(i - 1), i, roleBuild);
            }
        }
    }

    private void addArmourButtons(final List<Integer> slots) {
        for (int i = 0; i < 4; i++) {
            final Material material = this.getRole().getArmour().get(i);
            final int slot = slots.get(i);

            addButton(new BuildArmourButton(this, slot, new ItemStack(material)));
        }
    }

    private void addEquipButtons(final int slot, final int id, final RoleBuild roleBuild) {
        addButton(new BuildEquipButton(this, slot, id) {
            @Override
            public int getID() {
                return id;
            }

            @Override
            public RoleBuild getRoleBuild() {
                final Player player = this.getMenu().getPlayer();
                final Role role = this.getMenu().getRole();

                if (id == 0) {
                    return new DefaultRoleBuild(player, role) {
                        @Override
                        public boolean isActive() {
                            return BuildCustomizationMenu.this.getManager().getBuildsByRole(player, role).values().stream().noneMatch(RoleBuild::isActive);
                        }
                    };
                }

                return roleBuild;
            }
        });
    }

    private void addEditButtons(final int slot, final int id, final RoleBuild roleBuild) {
        addButton(new BuildEditButton(this, slot) {
            @Override
            public int getID() {
                return id;
            }

            @Override
            public RoleBuild getRoleBuild() {
                if (roleBuild == null) {
                    return new RoleBuild(this.getMenu().getPlayer(), this.getMenu().getRole(), id, false);
                }

                return roleBuild;
            }
        });
    }

    private void addDeleteButtons(final int slot, final int id, final RoleBuild roleBuild) {
        addButton(new BuildDeleteButton(this, slot) {
            @Override
            public int getID() {
                return id;
            }

            @Override
            public RoleBuild getRoleBuild() {
                return roleBuild;
            }
        });
    }
}