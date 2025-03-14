package me.trae.champions.donation;

import me.trae.champions.Champions;
import me.trae.champions.perk.PerkManager;
import me.trae.core.donation.abstracts.AbstractDonationManager;
import me.trae.core.utility.injectors.annotations.Inject;

public class DonationManager extends AbstractDonationManager<Champions, DonationRepository, PerkManager> {

    @Inject
    private PerkManager perkManager;

    public DonationManager(final Champions instance) {
        super(instance);
    }

    @Override
    public Class<DonationRepository> getClassOfRepository() {
        return DonationRepository.class;
    }

    @Override
    public PerkManager getPerkManager() {
        return this.perkManager;
    }
}