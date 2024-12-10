package me.trae.champions.donation;

import me.trae.champions.Champions;
import me.trae.core.donation.abstracts.AbstractDonationRepository;

public class DonationRepository extends AbstractDonationRepository<Champions, DonationManager> {

    public DonationRepository(final DonationManager manager) {
        super(manager);
    }
}