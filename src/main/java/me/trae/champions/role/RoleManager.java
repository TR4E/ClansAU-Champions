package me.trae.champions.role;

import me.trae.api.champions.role.Role;
import me.trae.api.champions.role.events.KitReceiveEvent;
import me.trae.champions.Champions;
import me.trae.champions.role.commands.KitCommand;
import me.trae.champions.role.interfaces.IRoleManager;
import me.trae.champions.role.modules.HandlePlayerRoleCheck;
import me.trae.champions.role.modules.HandleRoleEquip;
import me.trae.champions.role.modules.RemovePositivePotionEffectsOnRoleChange;
import me.trae.champions.role.modules.damage.HandleRoleCustomDamageSound;
import me.trae.champions.role.modules.displayname.HandleChampionsPlayerDisplayNameFormat;
import me.trae.champions.role.modules.item.HandleChampionsItemBuilderUpdate;
import me.trae.champions.role.modules.restrictions.DisableShootingArrowsForNonArchers;
import me.trae.champions.role.types.*;
import me.trae.champions.role.types.models.Archer;
import me.trae.champions.weapon.weapons.pvp.BoosterBow;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.framework.SpigotManager;
import me.trae.core.utility.UtilItem;
import me.trae.core.utility.UtilJava;
import me.trae.core.utility.UtilServer;
import me.trae.core.weapon.registry.WeaponRegistry;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class RoleManager extends SpigotManager<Champions> implements IRoleManager {

    private final Map<UUID, Role> PLAYER_ROLES = new HashMap<>();

    @ConfigInject(type = Boolean.class, path = "Starter-Kits", defaultValue = "false")
    private boolean starterKits;

    @ConfigInject(type = Boolean.class, path = "Overpowered-Kits", defaultValue = "false")
    private boolean overpoweredKits;

    public RoleManager(final Champions instance) {
        super(instance);
    }

    @Override
    public void registerModules() {
        // Roles
        addModule(new Assassin(this));
        addModule(new Brute(this));
        addModule(new Knight(this));
        addModule(new Mage(this));
        addModule(new Ranger(this));

        // Commands
        addModule(new KitCommand(this));

        // Modules
        addModule(new DisableShootingArrowsForNonArchers(this));
        addModule(new HandleChampionsPlayerDisplayNameFormat(this));
        addModule(new HandlePlayerRoleCheck(this));
        addModule(new HandleChampionsItemBuilderUpdate(this));
        addModule(new HandleRoleCustomDamageSound(this));
        addModule(new HandleRoleEquip(this));
        addModule(new RemovePositivePotionEffectsOnRoleChange(this));
    }

    @Override
    public Map<UUID, Role> getPlayerRoles() {
        return this.PLAYER_ROLES;
    }

    @Override
    public void setPlayerRole(final Player player, final Role role) {
        this.getPlayerRoles().put(player.getUniqueId(), role);
    }

    @Override
    public void removePlayerRole(final Player player) {
        this.getPlayerRoles().remove(player.getUniqueId());
    }

    @Override
    public Role getPlayerRole(final Player player) {
        return this.getPlayerRoles().getOrDefault(player.getUniqueId(), null);
    }

    @Override
    public boolean hasPlayerRole(final Player player) {
        return this.getPlayerRoles().containsKey(player.getUniqueId());
    }

    @Override
    public Role searchRole(final CommandSender sender, final String name, final boolean inform) {
        final List<Predicate<Role>> predicates = Arrays.asList(
                (role -> role.getName().equalsIgnoreCase(name)),
                (role -> role.getName().toLowerCase().contains(name.toLowerCase()))
        );

        final Function<Role, String> function = Role::getName;

        return UtilJava.search(this.getModulesByClass(Role.class), predicates, null, function, "Role Search", sender, name, inform);
    }

    @Override
    public void giveKit(final Player player, final Role role, boolean overpowered) {
        final KitReceiveEvent event = new KitReceiveEvent(player, role);
        UtilServer.callEvent(event);

        overpowered = this.overpoweredKits || event.isOverpowered() || overpowered;

        UtilItem.insert(player, new ItemStack(overpowered ? Material.DIAMOND_SWORD : Material.IRON_SWORD));
        UtilItem.insert(player, new ItemStack(overpowered ? Material.GOLD_AXE : Material.IRON_AXE));

        if (role instanceof Archer) {
            UtilItem.insert(player, overpowered ? WeaponRegistry.getWeaponByClass(BoosterBow.class).getFinalBuilder().toItemStack() : new ItemStack(Material.BOW));
            UtilItem.insert(player, new ItemStack(Material.ARROW, overpowered ? 64 : 32));
        }

        if (this.starterKits) {
            UtilItem.insert(player, new ItemStack(overpowered ? Material.DIAMOND_SPADE : Material.IRON_SPADE));
            UtilItem.insert(player, new ItemStack(overpowered ? Material.DIAMOND_PICKAXE : Material.IRON_PICKAXE));
        }

        UtilItem.insertArmour(player, new ItemStack(role.getArmour().get(0)), new ItemStack(role.getArmour().get(1)), new ItemStack(role.getArmour().get(2)), new ItemStack(role.getArmour().get(3)));
    }
}