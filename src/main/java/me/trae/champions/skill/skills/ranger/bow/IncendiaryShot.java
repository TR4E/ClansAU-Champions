package me.trae.champions.skill.skills.ranger.bow;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Ranger;
import me.trae.champions.skill.types.ActiveBowSkill;
import me.trae.champions.skill.types.data.BowSkillData;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class IncendiaryShot extends ActiveBowSkill<Ranger, BowSkillData> {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "30.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "12_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "1_500")
    private long duration;

    public IncendiaryShot(final Ranger module) {
        super(module);
    }

    @Override
    public Class<BowSkillData> getClassOfData() {
        return BowSkillData.class;
    }

    private long getDuration(final int level) {
        return level * this.duration;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Left-Click with a Bow to Prepare.",
                "",
                "Shoot an ignited arrow",
                UtilString.format("burning anyone hit for %s.", this.getValueString(Long.class, this::getDuration, level)),
                "",
                UtilString.pair("<gray>Recharge", UtilString.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", UtilString.format("<green>%s", this.getEnergyString(level)))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        this.addUser(new BowSkillData(player, level));

        super.onActivate(player, level);
    }

    @Override
    public void onHitByEntity(final Player damager, final Entity damagee, final CustomPostDamageEvent event, final BowSkillData data) {
        event.setReason(this.getDisplayName(data.getLevel()), this.getDuration(data.getLevel()));

        if (damagee instanceof Player) {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit <var> with <green><var></green>.", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel())));
            UtilMessage.simpleMessage(damagee, this.getModule().getName(), "<var> hit you with <green><var></green>.", Arrays.asList(event.getDamagerName(), this.getDisplayName(data.getLevel())));
        } else {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit a <var> with <green><var></green>.", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel())));
        }
    }

    @Override
    public void onUpdater(final Player player, final BowSkillData data) {
        data.getArrow().setFireTicks((int) ((this.getDuration(data.getLevel()) + 1000L) / 50));
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 2;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = level - 1;

        return this.recharge - (value * 1000L);
    }
}