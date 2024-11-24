package me.trae.champions.perk.perks;

import me.trae.champions.Champions;
import me.trae.champions.perk.PerkManager;
import me.trae.core.perk.Perk;

public class RaveArmour extends Perk<Champions, PerkManager> {

    public RaveArmour(final PerkManager manager) {
        super(manager);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Dynamic assassin leather armour.",
                "Cycles dye colors through every hue."
        };
    }
}