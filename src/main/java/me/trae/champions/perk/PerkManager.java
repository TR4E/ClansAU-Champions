package me.trae.champions.perk;

import me.trae.champions.Champions;
import me.trae.champions.donation.DonationManager;
import me.trae.champions.perk.perks.RaveArmour;
import me.trae.core.perk.abstracts.AbstractPerkManager;
import me.trae.core.utility.injectors.annotations.Inject;

public class PerkManager extends AbstractPerkManager<Champions, DonationManager> {

    @Inject
    private DonationManager donationManager;

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
        return this.donationManager;
    }
}