package me.trae.champions.gamer;

import me.trae.champions.Champions;
import me.trae.champions.gamer.enums.GamerProperty;
import me.trae.core.gamer.abstracts.AbstractGamerManager;
import me.trae.core.utility.objects.EnumData;
import org.bukkit.entity.Player;

public class GamerManager extends AbstractGamerManager<Champions, Gamer, GamerProperty, GamerRepository> {

    public GamerManager(final Champions instance) {
        super(instance);
    }

    @Override
    public Class<GamerRepository> getClassOfRepository() {
        return GamerRepository.class;
    }

    @Override
    public Gamer createGamer(final Player player) {
        return new Gamer(player);
    }

    @Override
    public Gamer createGamer(final EnumData<GamerProperty> data) {
        return new Gamer(data);
    }
}