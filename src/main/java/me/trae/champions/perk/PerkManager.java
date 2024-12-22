package me.trae.champions.perk;

import me.trae.champions.Champions;
import me.trae.champions.donation.DonationManager;
import me.trae.champions.perk.perks.RaveArmour;
import me.trae.core.perk.abstracts.AbstractPerkManager;

public class PerkManager extends AbstractPerkManager<Champions, DonationManager> {

    public PerkManager(final Champions instance) {
        super(instance);
    }

    @Override
    public void registerModules() {
        // Perks
        addModule(new RaveArmour(this));
    }

    @Override
    public DonationManager getDonationManager() {
        return this.getInstanceByClass(Champions.class).getManagerByClass(DonationManager.class);
    }
}