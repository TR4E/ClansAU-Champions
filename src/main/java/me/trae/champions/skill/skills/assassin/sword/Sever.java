package me.trae.champions.skill.skills.assassin.sword;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.effect.EffectManager;
import me.trae.champions.effect.types.Bleed;
import me.trae.champions.role.types.Assassin;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.effect.data.EffectData;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.UtilTime;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;
import java.util.Collections;

public class Sever extends ActiveSkill<Assassin, SkillData> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "20.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "20_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Prepare-Duration", defaultValue = "3_000")
    private long prepareDuration;

    public Sever(final Assassin module) {
        super(module, ActiveSkillType.SWORD);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private long getDuration(final int level) {
        return level * 1000L;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with a Sword to Activate.",
                "",
                String.format("Your next hit applies a <green>%s</green> bleed,", UtilTime.getTime(this.getDuration(level))),
                "dealing 1 heart per second.",
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyString(level)))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        this.addUser(new SkillData(player, level, this.prepareDuration));

        new SoundCreator(Sound.ZOMBIE_REMEDY, 2.5F, 0.5F).play(player.getLocation());

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You have prepared <green><var></green>", Collections.singletonList(this.getDisplayName(level)));
    }

    @Override
    public void onExpire(final Player player, final SkillData data) {
        new SoundCreator(Sound.NOTE_STICKS).play(player);

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You failed <green><var></green>", Collections.singletonList(this.getDisplayName(data.getLevel())));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCustomPostDamage(final CustomPostDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        if (!(event.getDamagee() instanceof Player)) {
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

        final Player damagee = event.getDamageeByClass(Player.class);

        this.getInstance().getManagerByClass(EffectManager.class).getModuleByClass(Bleed.class).addUser(new EffectData(damagee, this.getDuration(data.getLevel())) {
            @Override
            public LivingEntity getCause() {
                return damager;
            }
        });

        event.setReason(this.getDisplayName(data.getLevel()), this.getDuration(data.getLevel()));

        UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit <var> with <green><var></green>.", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel())));
        UtilMessage.simpleMessage(damagee, this.getModule().getName(), "<var> hit you with <green><var></green>.", Arrays.asList(event.getDamagerName(), this.getDisplayName(data.getLevel())));

        this.removeUser(damager);
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 3;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = (level - 1) * 2;

        return this.recharge - (value * 1000L);
    }
}