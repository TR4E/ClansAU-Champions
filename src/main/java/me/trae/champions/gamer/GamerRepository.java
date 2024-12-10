package me.trae.champions.gamer;

import me.trae.champions.Champions;
import me.trae.champions.gamer.enums.GamerProperty;
import me.trae.core.gamer.abstracts.AbstractGamerRepository;

public class GamerRepository extends AbstractGamerRepository<Champions, GamerManager, Gamer, GamerProperty> {

    public GamerRepository(final GamerManager manager) {
        super(manager);
    }
}