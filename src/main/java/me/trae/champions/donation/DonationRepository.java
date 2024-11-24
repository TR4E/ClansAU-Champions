package me.trae.champions.donation;

import me.trae.champions.Champions;
import me.trae.champions.config.Config;
import me.trae.core.donation.abstracts.AbstractDonationRepository;

public class DonationRepository extends AbstractDonationRepository<Champions, DonationManager, Config> {

    public DonationRepository(final DonationManager manager) {
        super(manager);
    }

    @Override
    public Class<Config> getClassOfConfiguration() {
        return Config.class;
    }
}