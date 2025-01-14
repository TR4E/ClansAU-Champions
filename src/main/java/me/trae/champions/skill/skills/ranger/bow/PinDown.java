package me.trae.champions.skill.skills.ranger.bow;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Ranger;
import me.trae.champions.skill.skills.ranger.bow.data.PinDownData;
import me.trae.champions.skill.types.ActiveBowSkill;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.*;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;

public class PinDown extends ActiveBowSkill<Ranger, PinDownData> {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "35.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "13_000")
    private long recharge;

    @ConfigInject(type = Double.class, path = "Arrow-Velocity", defaultValue = "1.6")
    private double arrowVelocity;

    @ConfigInject(type = Integer.class, path = "Arrow-Count", defaultValue = "1")
    private int arrowCount;

    public PinDown(final Ranger module) {
        super(module);
    }

    @Override
    public Class<PinDownData> getClassOfData() {
        return PinDownData.class;
    }

    private int getAmplifier(final int level) {
        return level + 1;
    }

    private long getDuration(final int level) {
        return (long) ((level * 1.5D) * 1000L);
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Left-Click with a Bow to Activate.",
                "",
                String.format("You will instantly fire %s arrow(s)", this.getValueString(Integer.class, this.arrowCount)),
                String.format("which gives anybody hit Slowness %s", this.getValueString(Integer.class, this::getAmplifier, level)),
                String.format("for %s.", this.getValueString(Long.class, this::getDuration, level)),
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyString(level)))
        };
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void onActivate(final Player player, final int level) {
        UtilItem.remove(player, new ItemStack(Material.ARROW), this.arrowCount);

        final PinDownData data = new PinDownData(player, level);

        for (int i = 0; i < this.arrowCount; i++) {
            final Arrow arrow = player.launchProjectile(Arrow.class);

            arrow.setVelocity(player.getLocation().getDirection().multiply(this.arrowVelocity));

            data.setArrow(arrow);
            data.addExtraArrow(arrow);

            player.getWorld().playEffect(player.getLocation(), Effect.BOW_FIRE, 0);
        }

        this.addUser(data);

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @Override
    public boolean canActivate(final Player player) {
        if (!(super.canActivate(player))) {
            return false;
        }

        if (!(UtilItem.contains(player, new ItemStack(Material.ARROW), this.arrowCount))) {
            UtilMessage.simpleMessage(player, this.getModule().getName(), "You cannot use <green><var></green> without having atleast <green><var> Arrow(s)</green>.", Arrays.asList(this.getName(), String.valueOf(this.arrowCount)));
            return false;
        }

        return true;
    }

    @Override
    public void onPrepare(final Player player, final int level) {
    }

    @Override
    public void onFire(final Player player, final PinDownData data) {
    }

    @Override
    public void onHitByEntity(final Player damager, final Entity damagee, final CustomPostDamageEvent event, final PinDownData data) {
        if (!(damagee instanceof LivingEntity)) {
            return;
        }

        event.setLightReason(this.getDisplayName(data.getLevel()), 1000L);

        final LivingEntity damageeLivingEntity = UtilJava.cast(LivingEntity.class, damagee);

        UtilEntity.givePotionEffect(damageeLivingEntity, PotionEffectType.SLOW, this.getAmplifier(data.getLevel()), this.getDuration(data.getLevel()));
    }

    @Override
    public float getEnergy(final int level) {
        final int value = level - 1;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = (int) ((level - 1) * 1.5D);

        return this.recharge - (value * 1000L);
    }
}