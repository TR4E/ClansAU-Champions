package me.trae.champions.skill.skills.mage.sword;

import me.trae.api.champions.skill.events.SkillFriendlyFireEvent;
import me.trae.api.champions.skill.events.SkillLocationEvent;
import me.trae.api.damage.utility.UtilDamage;
import me.trae.champions.role.types.Mage;
import me.trae.champions.skill.data.types.ChannelSkillData;
import me.trae.champions.skill.types.ChannelSkill;
import me.trae.core.Core;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.throwable.Throwable;
import me.trae.core.throwable.ThrowableManager;
import me.trae.core.throwable.events.ThrowableCollideEntityEvent;
import me.trae.core.utility.UtilMath;
import me.trae.core.utility.UtilServer;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Inferno extends ChannelSkill<Mage, ChannelSkillData> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy-Needed", defaultValue = "6.0")
    private float energyNeeded;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "3_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "3_000")
    private long duration;

    @ConfigInject(type = Long.class, path = "Fire-Duration", defaultValue = "4_000")
    private long fireDuration;

    @ConfigInject(type = Boolean.class, path = "Friendly-Fire", defaultValue = "false")
    private boolean friendlyFire;

    @ConfigInject(type = String.class, path = "Material", defaultValue = "BLAZE_POWDER")
    private String material;

    public Inferno(final Mage module) {
        super(module);
    }

    @Override
    public Class<ChannelSkillData> getClassOfData() {
        return ChannelSkillData.class;
    }

    private Material getMaterial() {
        try {
            return Material.valueOf(this.material);
        } catch (final Exception ignored) {
        }

        return Material.BLAZE_POWDER;
    }

    private long getDuration(final int level) {
        return this.duration;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Hold Block with a Sword to Channel.",
                "",
                "You spray fire at high speed",
                "igniting those it hits.",
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyUsingString(level)))
        };
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void onActivate(final Player player, final int level) {
        this.addUser(new ChannelSkillData(player, level));
    }

    @Override
    public void onUsing(final Player player, final ChannelSkillData data) {
        final Throwable throwable = new Throwable(this.getDisplayName(data.getLevel()), new ItemStack(this.getMaterial()), player, this.getDuration(data.getLevel()), player.getEyeLocation(), player.getEyeLocation().getDirection().add(new Vector(UtilMath.getRandomNumber(Double.class, -0.2D, 0.2D), UtilMath.getRandomNumber(Double.class, -0.2D, 0.3D), UtilMath.getRandomNumber(Double.class, -0.2D, 0.2D))));

        this.getInstance(Core.class).getManagerByClass(ThrowableManager.class).addThrowable(throwable);

        new SoundCreator(Sound.GHAST_FIREBALL, 0.1F, 1.0F).play(player.getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onThrowableCollideEntity(final ThrowableCollideEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Throwable throwable = event.getThrowable();

        if (!(throwable.getName().startsWith(this.getName()))) {
            return;
        }

        final Player throwerPlayer = throwable.getThrowerPlayer();

        final LivingEntity target = event.getTarget();

        if (UtilServer.getEvent(new SkillLocationEvent(this, target.getLocation())).isCancelled()) {
            return;
        }

        if (target instanceof Player) {
            final SkillFriendlyFireEvent friendlyFireEvent = new SkillFriendlyFireEvent(this, throwerPlayer, event.getTargetByClass(Player.class));
            UtilServer.callEvent(friendlyFireEvent);
            if (friendlyFireEvent.isCancelled()) {
                return;
            }

            if (!(this.friendlyFire)) {
                if (target == throwerPlayer) {
                    return;
                }

                if (!(friendlyFireEvent.isVulnerable())) {
                    return;
                }
            }
        }

        if (throwable.isCollided(target)) {
            return;
        }

        UtilDamage.damage(target, throwerPlayer, EntityDamageEvent.DamageCause.CUSTOM, 1.0D, throwable.getName(), 1000L);

        target.setFireTicks((int) (this.fireDuration / 50L));

        throwable.addCollided(target, 500L);
    }

    @Override
    public float getEnergy(final int level) {
        return 0.0F;
    }

    @Override
    public float getEnergyNeeded(final int level) {
        final int value = level - 1;

        return this.energyNeeded - value;
    }

    @Override
    public float getEnergyUsing(final int level) {
        return this.getEnergyNeeded(level) / 2;
    }

    @Override
    public long getRecharge(final int level) {
        return this.recharge;
    }
}