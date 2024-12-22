package me.trae.champions.donation;

import me.trae.champions.Champions;
import me.trae.champions.perk.PerkManager;
import me.trae.core.donation.abstracts.AbstractDonationManager;

public class DonationManager extends AbstractDonationManager<Champions, DonationRepository, PerkManager> {

    public DonationManager(final Champions instance) {
        super(instance);
    }

    @Override
    public Class<DonationRepository> getClassOfRepository() {
        return DonationRepository.class;
    }

    @Override
    public PerkManager getPerkManager() {
        return this.getInstanceByClass(Champions.class).getManagerByClass(PerkManager.class);
    }
}