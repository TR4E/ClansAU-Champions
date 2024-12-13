package me.trae.champions.skill.skills.ranger.axe;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Ranger;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.UtilEntity;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.UtilTime;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class WolfsFury extends ActiveSkill<Ranger, SkillData> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "40.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "25_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "4_000")
    private long duration;

    @ConfigInject(type = Integer.class, path = "Amplifier", defaultValue = "2")
    private int amplifier;

    public WolfsFury(final Ranger module) {
        super(module, ActiveSkillType.AXE);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with an Axe to Activate.",
                "",
                "Summon the power of the wolf, gaining",
                String.format("Strength %s for %s, and giving", this.amplifier, UtilTime.getTime(this.duration)),
                "no knockback on your attacks.",
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyString(level)))

        };
    }

    @Override
    public int getDefaultLevel() {
        return 2;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void onActivate(final Player player, final int level) {
        UtilEntity.givePotionEffect(player, PotionEffectType.INCREASE_DAMAGE, this.amplifier, this.duration);

        new SoundCreator(Sound.WOLF_HOWL, 2.0F, 2.0F).play(player.getLocation());

        this.addUser(new SkillData(player, level, this.duration));

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You activated <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @Override
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            UtilEntity.removePotionEffect(player, PotionEffectType.INCREASE_DAMAGE);
        }

        super.reset(player);
    }

    @Override
    public void onExpire(final Player player, final SkillData data) {
        new SoundCreator(Sound.NOTE_STICKS).play(player);

        UtilMessage.simpleMessage(player, this.getModule().getName(), "<green><var></green> has ended.", Collections.singletonList(this.getDisplayName(data.getLevel())));
    }

    @EventHandler
    public void onCustomPostDamage(final CustomPostDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        final Player damager = event.getDamagerByClass(Player.class);

        final SkillData data = this.getUserByPlayer(damager);
        if (data == null) {
            return;
        }

        new SoundCreator(Sound.WOLF_HOWL, 2.0F, 2.0F).play(damager.getLocation());

        event.setReason(this.getDisplayName(data.getLevel()), this.duration);
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 2;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = (int) ((level - 1) * 1.5D);

        return this.recharge - value;
    }
}