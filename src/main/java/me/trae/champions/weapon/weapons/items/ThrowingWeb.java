package me.trae.champions.weapon.weapons.items;

import me.trae.champions.Champions;
import me.trae.champions.weapon.WeaponManager;
import me.trae.core.Core;
import me.trae.core.blockrestore.BlockRestore;
import me.trae.core.blockrestore.BlockRestoreManager;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.throwable.Throwable;
import me.trae.core.throwable.ThrowableManager;
import me.trae.core.throwable.events.ThrowableGroundedEvent;
import me.trae.core.throwable.events.ThrowableUpdaterEvent;
import me.trae.core.utility.UtilBlock;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilServer;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.enums.ActionType;
import me.trae.core.weapon.data.WeaponData;
import me.trae.core.weapon.events.WeaponLocationEvent;
import me.trae.core.weapon.types.ActiveCustomItem;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ThrowingWeb extends ActiveCustomItem<Champions, WeaponManager, WeaponData> implements Listener {

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "10_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "5_000")
    private long duration;

    @ConfigInject(type = Double.class, path = "Item-Velocity", defaultValue = "1.8")
    private double itemVelocity;

    @ConfigInject(type = Integer.class, path = "Particle-Count", defaultValue = "8")
    private int particleCount;

    @ConfigInject(type = Boolean.class, path = "Trap-Corners", defaultValue = "false")
    private boolean trapCorners;

    public ThrowingWeb(final WeaponManager manager) {
        super(manager, new ItemStack(Material.WEB), ActionType.LEFT_CLICK);
    }

    @Override
    public int getModel() {
        return 575091;
    }

    @Override
    public Class<WeaponData> getClassOfData() {
        return WeaponData.class;
    }

    @Override
    public boolean isNaturallyObtained() {
        return true;
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Creates a Web Trap.",
                "",
                UtilString.pair("<gray>Left-Click", "<yellow>Throw"),
        };
    }

    @Override
    public void onActivate(final Player player, final ActionType actionType) {
        final Throwable throwable = new Throwable(this.getAbilityName(), this.getItemStack(), player, 10_000L, player.getEyeLocation(), player.getEyeLocation().getDirection().multiply(this.itemVelocity));

        this.getInstanceByClass(Core.class).getManagerByClass(ThrowableManager.class).addThrowable(throwable);

        UtilMessage.simpleMessage(player, "Item", "You threw a <var>.", Collections.singletonList(this.getDisplayName()));
    }

    @EventHandler
    public void onThrowableUpdater(final ThrowableUpdaterEvent event) {
        if (!(event.isThrowableName(this.getAbilityName()))) {
            return;
        }

        final Item item = event.getThrowable().getItem();
        if (item == null) {
            return;
        }

        final Random random = new Random();

        if (UtilServer.getEvent(new WeaponLocationEvent(this, item.getLocation())).isCancelled()) {
            return;
        }

        for (int i = 0; i < this.particleCount; i++) {
            item.getWorld().playEffect(item.getLocation().add(0.0D, random.nextGaussian(), 0.0D), Effect.TILE_BREAK, this.getItemStack().getType().getId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onThrowableGrounded(final ThrowableGroundedEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.isThrowableName(this.getAbilityName()))) {
            return;
        }

        event.setCancelled(true);

        final Location location = event.getLocation();

        if (UtilServer.getEvent(new WeaponLocationEvent(this, location)).isCancelled()) {
            return;
        }

        final BlockRestoreManager blockRestoreManager = this.getInstanceByClass(Core.class).getManagerByClass(BlockRestoreManager.class);

        for (final Block block : this.getBlocks(location)) {
            if (UtilServer.getEvent(new WeaponLocationEvent(this, block.getLocation())).isCancelled()) {
                continue;
            }

            final BlockRestore blockRestore = new BlockRestore(this.getAbilityName(), block, this.getItemStack().getType(), (byte) 0, this.duration) {
                @Override
                public boolean isSaveToRepository() {
                    return true;
                }
            };

            blockRestoreManager.addBlockRestore(blockRestore);
        }
    }

    public List<Block> getBlocks(final Location location) {
        final List<Block> list = new ArrayList<>();

        final int centerX = location.getBlockX();
        final int centerY = location.getBlockY();
        final int centerZ = location.getBlockZ();

        final World world = location.getWorld();

        // Center
        list.add(world.getBlockAt(centerX, centerY, centerZ));
        // Left
        list.add(world.getBlockAt(centerX - 1, centerY, centerZ));
        // Right
        list.add(world.getBlockAt(centerX + 1, centerY, centerZ));
        // Back
        list.add(world.getBlockAt(centerX, centerY, centerZ - 1));
        // Front
        list.add(world.getBlockAt(centerX, centerY, centerZ + 1));
        // Top Center
        list.add(world.getBlockAt(centerX, centerY + 1, centerZ));

        if (this.trapCorners) {
            // Back-left corner
            list.add(world.getBlockAt(centerX - 1, centerY, centerZ - 1));
            // Front-left corner
            list.add(world.getBlockAt(centerX - 1, centerY, centerZ + 1));
            // Back-right corner
            list.add(world.getBlockAt(centerX + 1, centerY, centerZ - 1));
            // Front-right corner
            list.add(world.getBlockAt(centerX + 1, centerY, centerZ + 1));
        }

        list.removeIf(block -> block == null || !UtilBlock.airFoliage(block.getType()));

        return list;
    }

    @Override
    public float getEnergy(final ActionType actionType) {
        return 0.0F;
    }

    @Override
    public long getRecharge(final ActionType actionType) {
        return this.recharge;
    }
}