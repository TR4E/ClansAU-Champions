package me.trae.champions.gamer;

import me.trae.champions.Champions;
import me.trae.champions.config.Config;
import me.trae.champions.gamer.enums.GamerProperty;
import me.trae.core.gamer.abstracts.AbstractGamerRepository;

public class GamerRepository extends AbstractGamerRepository<Champions, GamerManager, Config, Gamer, GamerProperty> {

    public GamerRepository(final GamerManager manager) {
        super(manager);
    }

    @Override
    public Class<Config> getClassOfConfiguration() {
        return Config.class;
    }
}