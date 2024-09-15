package me.trae.champions.skill.skills.knight;

import me.trae.api.damage.events.CustomDamageEvent;
import me.trae.champions.role.types.Knight;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.core.utility.UtilBlock;
import me.trae.core.utility.UtilEntity;
import me.trae.core.utility.UtilMessage;
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

        this.addPrimitive("Duration", 4000L);
        this.addPrimitive("Amplifier", 2);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    @Override
    public String getDisplayName(final int level) {
        return String.format("%s %s", this.getName(), level);
    }

    @Override
    public void onActivate(final Player player, final int level) {
        final long duration = this.getPrimitiveCasted(Long.class, "Duration");

        this.addUser(new SkillData(player, level, duration));

        UtilEntity.givePotionEffect(player, PotionEffectType.SPEED, duration, this.getPrimitiveCasted(Integer.class, "Amplifier"));

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @Override
    public boolean canActivate(final Player player) {
        if (UtilBlock.isInLiquid(player.getLocation())) {
            UtilMessage.simpleMessage(player, "Skill", "You cannot use <green><var></green> while in liquid.", Collections.singletonList(this.getName()));
            return false;
        }

        return true;
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

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        final Player player = event.getDamagerByClass(Player.class);

        if (!(this.isUserByPlayer(player))) {
            return;
        }

        final String displayName = this.getDisplayName(this.getUserByPlayer(player).getLevel());

        this.reset(player);

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You hit <yellow><var></yellow> with <green><var></green>.", Arrays.asList(event.getDamageeName(), displayName));

        event.setReason(displayName, 10_000L);
    }
}