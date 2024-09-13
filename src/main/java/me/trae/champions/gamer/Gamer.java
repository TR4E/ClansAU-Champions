package me.trae.champions.gamer;

import me.trae.core.gamer.abstracts.AbstractGamer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Gamer extends AbstractGamer {

    public Gamer(final UUID uuid) {
        super(uuid);
    }

    public Gamer(final Player player) {
        this(player.getUniqueId());
    }
}