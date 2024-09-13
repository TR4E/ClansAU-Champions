package me.trae.champions.gamer;

import me.trae.champions.Champions;
import me.trae.core.gamer.abstracts.AbstractGamerManager;
import org.bukkit.entity.Player;

public class GamerManager extends AbstractGamerManager<Champions, Gamer> {

    public GamerManager(final Champions instance) {
        super(instance);
    }

    @Override
    public Gamer createGamer(final Player player) {
        return new Gamer(player);
    }
}