package me.trae.champions.skill.skills.knight;

import me.trae.api.damage.events.CustomDamageEvent;
import me.trae.champions.role.types.Knight;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.core.utility.UtilEntity;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilTime;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;

public class BullsCharge extends ActiveSkill<Knight, SkillData> implements Listener {

    public BullsCharge(final Knight module) {
        super(module, SkillType.AXE);

        this.addPrimitive("Amplifier", 2);
        this.addPrimitive("Duration", 4000L);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    @Override
    public String[] getDescription(final int level) {
        final String duration = UtilTime.getTime(this.getPrimitiveCasted(Long.class, "Duration"));

        return new String[]{
                "Right-Click with an Axe to Activate.",
                "",
                "Enter a rage, gaining massive movement speed",
                String.format("and slowing anything you hit for <green>%s</green>.", duration),
                "",
                "While charging, you take no knockback.",
//                "",
//                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString())),
//                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyString()))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, Material.OBSIDIAN);

        new SoundCreator(Sound.ENDERMAN_SCREAM, 1.5F, 0.0F).play(player.getLocation());

        final long duration = this.getPrimitiveCasted(Long.class, "Duration");

        UtilEntity.givePotionEffect(player, PotionEffectType.SPEED, duration, this.getPrimitiveCasted(Integer.class, "Amplifier"));

        this.addUser(new SkillData(player, level, duration));

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @Override
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            UtilEntity.removePotionEffect(player, PotionEffectType.SPEED);
        }

        super.reset(player);
    }

    @Override
    public void onExpire(final Player player, final SkillData data) {
        UtilMessage.simpleMessage(player, this.getModule().getName(), "You failed <green><var></green>.", Collections.singletonList(this.getDisplayName(data.getLevel())));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCustomDamage(final CustomDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamagee() instanceof LivingEntity)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        final Player damager = event.getDamagerByClass(Player.class);

        if (!(this.isUserByPlayer(damager))) {
            return;
        }

        final LivingEntity damagee = event.getDamageeByClass(LivingEntity.class);

        UtilEntity.givePotionEffect(damagee, PotionEffectType.SLOW, this.getPrimitiveCasted(Long.class, "Duration"), this.getPrimitiveCasted(Integer.class, "Amplifier"));

        new SoundCreator(Sound.ENDERMAN_SCREAM, 1.5F, 0.0F).play(damager.getLocation());
        new SoundCreator(Sound.ZOMBIE_METAL, 1.5F, 0.5F).play(damager.getLocation());

        if (damagee instanceof Player) {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit <var> with <green><var></green>.", Arrays.asList(event.getDamageeName(), this.getName()));
            UtilMessage.simpleMessage(damagee, this.getModule().getName(), "<var> hit you with <green><var></green>.", Arrays.asList(event.getDamagerName(), this.getName()));
        } else {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit a <var> with <green><var></green>.", Arrays.asList(event.getDamageeName(), this.getName()));
        }

        this.reset(damager);
        this.removeUser(damager);

        event.setReason(this.getName(), this.getPrimitiveCasted(Long.class, "Duration"));
    }
}