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
import me.trae.core.utility.UtilString;
import me.trae.core.utility.UtilTime;
import me.trae.core.utility.enums.ActionType;
import me.trae.core.weapon.data.WeaponData;
import me.trae.core.weapon.types.ActiveCustomItem;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
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

    @ConfigInject(type = Integer.class, path = "Particle-Count", defaultValue = "50")
    private int particleCount;

    public ThrowingWeb(final WeaponManager manager) {
        super(manager, new ItemStack(Material.WEB), ActionType.LEFT_CLICK);
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
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", UtilTime.getTime(this.recharge))),
                UtilString.pair("<gray>Duration", String.format("<green>%s", UtilTime.getTime(this.duration)))
        };
    }

    @Override
    public void onActivate(final Player player, final ActionType actionType) {
        final Throwable throwable = new Throwable(this.getAbilityName(), this.getItemStack(), player, this.duration, player.getLocation().getDirection().multiply(this.itemVelocity));

        this.getInstance(Core.class).getManagerByClass(ThrowableManager.class).addThrowable(throwable);

        UtilMessage.simpleMessage(player, "Item", "You threw a <var>.", Collections.singletonList(this.getDisplayName()));
    }

    @EventHandler
    public void onThrowableUpdater(final ThrowableUpdaterEvent event) {
        if (!(event.isThrowableName(this.getAbilityName()))) {
            return;
        }

        final Item item = event.getThrowable().getItem();

        final Random random = new Random();

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

        final BlockRestoreManager blockRestoreManager = this.getInstance(Core.class).getManagerByClass(BlockRestoreManager.class);

        for (final Block block : this.getBlocks(event.getLocation())) {
            final BlockRestore blockRestore = new BlockRestore(this.getAbilityName(), block, this.getItemStack().getType(), (byte) 0, this.duration) {
                @Override
                public boolean isSaveToRepository() {
                    return true;
                }
            };

            blockRestoreManager.addBlockRestore(blockRestore);
        }
    }

    public List<Block> getBlocks(final Location locationm) {
        final List<Block> list = new ArrayList<>();

        final int startX = locationm.getBlockX() - 1;
        final int startZ = locationm.getBlockZ() - 1;
        final int centerY = locationm.getBlockY();

        for (int x = startX; x <= startX + 2; x++) {
            for (int z = startZ; z <= startZ + 2; z++) {
                list.add(new Location(locationm.getWorld(), x, centerY, z).getBlock());
            }
        }

        list.add(new Location(locationm.getWorld(), locationm.getBlockX(), centerY + 1, locationm.getBlockZ()).getBlock());

        list.removeIf(block -> {
            if (block == null) {
                return true;
            }

            if (!(UtilBlock.airFoliage(block.getType()))) {
                return true;
            }

            return false;
        });

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