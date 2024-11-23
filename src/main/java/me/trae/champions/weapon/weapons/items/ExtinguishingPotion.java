package me.trae.champions.weapon.weapons.items;

import me.trae.champions.Champions;
import me.trae.champions.weapon.WeaponManager;
import me.trae.core.Core;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.throwable.Throwable;
import me.trae.core.throwable.ThrowableManager;
import me.trae.core.utility.*;
import me.trae.core.utility.enums.ActionType;
import me.trae.core.weapon.data.WeaponData;
import me.trae.core.weapon.events.WeaponLocationEvent;
import me.trae.core.weapon.types.ActiveCustomItem;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class ExtinguishingPotion extends ActiveCustomItem<Champions, WeaponManager, WeaponData> {

    @ConfigInject(type = Long.class, path = "Left-Click-Recharge", defaultValue = "5_000")
    private long leftClickRecharge;

    @ConfigInject(type = Long.class, path = "Right-Click-Recharge", defaultValue = "5_000")
    private long rightClickRecharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "4_000")
    private long duration;

    @ConfigInject(type = Integer.class, path = "Amplifier", defaultValue = "1")
    private int amplifier;

    @ConfigInject(type = Double.class, path = "Velocity", defaultValue = "1.8")
    private double velocity;

    public ExtinguishingPotion(final WeaponManager manager) {
        super(manager, new ItemStack(Material.POTION, 1, (short) 0), ActionType.ALL);
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
                UtilString.pair("<gray>Left-Click", "<yellow>Throw"),
                "   Douses Players.",
                "   Douses Fires.",
                "",
                UtilString.pair("<gray>Right-Click", "<yellow>Drink"),
                "   Douses Self.",
                String.format("   Fire Resistance <green>%s</green> for <green>%s</green>.", this.amplifier, UtilTime.getTime(this.duration)),
        };
    }

    @Override
    public String getAbilityName(final ActionType actionType) {
        switch (actionType) {
            case LEFT_CLICK:
                return "Extinguish Throw";
            case RIGHT_CLICK:
                return "Extinguish Drink";
        }

        return this.getAbilityName();
    }

    @Override
    public void onActivate(final Player player, final ActionType actionType) {
        switch (actionType) {
            case LEFT_CLICK: {
                this.onLeftClick(player);
                break;
            }
            case RIGHT_CLICK: {
                this.onRightClick(player);
                break;
            }
        }
    }

    private void onLeftClick(final Player player) {
        final Throwable throwable = new Throwable(this.getAbilityName(ActionType.LEFT_CLICK), this.getItemStack(), player, -1L, player.getLocation().getDirection().multiply(this.velocity)) {
            @Override
            public void onGrounded(final Location location) {
                for (final Block block : UtilBlock.getInRadius(location, 3)) {
                    if (block.getType() == Material.AIR || UtilBlock.isInLiquid(block.getLocation())) {
                        continue;
                    }

                    if (block.getType() == Material.FIRE) {
                        block.setType(Material.AIR);
                        block.getWorld().playEffect(block.getLocation(), Effect.EXTINGUISH, 1);
                    }

                    UtilBlock.splash(location.getBlock().getRelative(BlockFace.DOWN).getLocation());
                }
            }

            @Override
            public boolean canGrounded(final Location location) {
                return !(UtilServer.getEvent(new WeaponLocationEvent(ExtinguishingPotion.this, location)).isCancelled());
            }

            @Override
            public boolean removeOnGrounded(final Location location) {
                return true;
            }

            @Override
            public void onEntityCollide(final LivingEntity entity) {
                entity.setFireTicks(0);

                UtilEntity.givePotionEffect(entity, PotionEffectType.FIRE_RESISTANCE, ExtinguishingPotion.this.amplifier, ExtinguishingPotion.this.duration);

                UtilBlock.splash(entity.getEyeLocation());
            }
        };

        this.getInstance(Core.class).getManagerByClass(ThrowableManager.class).addThrowable(throwable);

        UtilMessage.simpleMessage(player, "Item", "You threw a <var>.", Collections.singletonList(this.getDisplayName()));
    }

    private void onRightClick(final Player player) {
        player.setFireTicks(0);

        UtilEntity.givePotionEffect(player, PotionEffectType.FIRE_RESISTANCE, ExtinguishingPotion.this.amplifier, ExtinguishingPotion.this.duration);

        UtilBlock.splash(player.getEyeLocation());

        UtilMessage.simpleMessage(player, "Item", "You consumed a <var>.", Collections.singletonList(this.getDisplayName()));
    }

    @Override
    public float getEnergy(final ActionType actionType) {
        return 0.0F;
    }

    @Override
    public long getRecharge(final ActionType actionType) {
        switch (actionType) {
            case LEFT_CLICK:
                return this.leftClickRecharge;
            case RIGHT_CLICK:
                return this.rightClickRecharge;
        }

        return 0L;
    }
}