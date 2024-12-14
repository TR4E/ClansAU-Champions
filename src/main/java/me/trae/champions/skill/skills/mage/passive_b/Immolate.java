package me.trae.champions.skill.skills.mage.passive_b;

import me.trae.champions.role.types.Mage;
import me.trae.champions.skill.data.types.ToggleUpdaterDropSkillData;
import me.trae.champions.skill.types.ToggleUpdaterDropSkill;
import me.trae.champions.skill.types.models.ToggleSkill;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import org.bukkit.entity.Player;

public class Immolate extends ToggleUpdaterDropSkill<Mage, ToggleUpdaterDropSkillData> implements ToggleSkill<ToggleUpdaterDropSkillData> {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "10.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "5_000")
    private long recharge;

    @ConfigInject(type = Integer.class, path = "Speed-Amplifier", defaultValue = "2")
    private int speedAmplifier;

    public Immolate(final Mage module) {
        super(module);
    }

    @Override
    public Class<ToggleUpdaterDropSkillData> getClassOfData() {
        return ToggleUpdaterDropSkillData.class;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Drop Sword/Axe to Toggle.",
                "",
                "Igniting yourself in flaming fury.",
                String.format("You receive Speed %s and Fire Resistance", this.speedAmplifier),
                "",
                "You leave a trail of fire, which",
                "burns players that go near it.",
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyString(level)))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        this.addUser(new ToggleUpdaterDropSkillData(player, level));

        UtilMessage.simpleMessage(player, this.getModule().getName(), UtilString.pair(this.getName(), "<green>On"));
    }

    @Override
    public void onDeActivate(final Player player, final ToggleUpdaterDropSkillData data) {
        UtilMessage.simpleMessage(player, this.getModule().getName(), UtilString.pair(this.getName(), "<red>Off"));
    }

    @Override
    public void onUsing(final Player player, final ToggleUpdaterDropSkillData data) {
    }

    @Override
    public float getEnergy(final int level) {
        return 0.0F;
    }

    @Override
    public float getEnergyNeeded(final int level) {
        return this.energy;
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