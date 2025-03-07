package me.trae.champions.skill.skills.global;

import me.trae.api.champions.role.Role;
import me.trae.champions.skill.skills.global.modules.SwimAbility;
import me.trae.champions.skill.types.GlobalSkill;
import me.trae.core.utility.UtilString;

public class Swim extends GlobalSkill<SwimAbility> {

    public Swim(final Role module) {
        super(module);
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Tap Crouch to Swim forwards.",
                "",
                UtilString.pair("<gray>Energy", UtilString.format("<green>%s", this.getAbility().getEnergy(level)))
        };
    }

    @Override
    public Class<SwimAbility> getClassOfAbility() {
        return SwimAbility.class;
    }

    @Override
    public int getDefaultLevel() {
        return 2;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}