package me.trae.champions.skill.skills.assassin.axe;

import me.trae.champions.role.types.Assassin;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.UtilLeap;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.components.SelfManagedAbilityComponent;
import org.bukkit.entity.Player;

public class Leap extends ActiveSkill<Assassin, SkillData> implements SelfManagedAbilityComponent {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "30.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Leap-Recharge", defaultValue = "5_000")
    private long leapRecharge;

    @ConfigInject(type = Long.class, path = "WallKick-Recharge", defaultValue = "150")
    private long wallKickRecharge;

    public Leap(final Assassin module) {
        super(module, ActiveSkillType.AXE);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    @Override
    public int getDefaultLevel() {
        return 2;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with an Axe to Activate.",
                "",
                "You take a great Leap.",
                "",
                UtilString.pair("<gray>Recharge", String.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", String.format("<green>%s", this.getEnergyString(level)))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        final String prefix = this.getModule().getName();

        final String leapAbilityName = this.getDisplayName(level);
        final String wallKickAbilityName = String.format("Wall Kick %s", level);

        final String leapCooldownName = this.getName();
        final String wallKickCooldownName = "Wall Kick";

        final long leapRechargeDuration = this.getRecharge(level);
        final long wallKickRechargeDuration = this.wallKickRecharge;

        final float energy = this.getEnergy(level);

        UtilLeap.activate(player, prefix, leapAbilityName, wallKickAbilityName, leapCooldownName, wallKickCooldownName, leapRechargeDuration, wallKickRechargeDuration, energy);
    }

    @Override
    public float getEnergy(final int level) {
        return this.energy;
    }

    @Override
    public long getRecharge(final int level) {
        return this.leapRecharge;
    }
}