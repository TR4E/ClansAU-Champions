package me.trae.champions.gamer;

import me.trae.champions.gamer.enums.GamerProperty;
import me.trae.core.gamer.abstracts.AbstractGamer;
import me.trae.core.utility.objects.EnumData;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class Gamer extends AbstractGamer<GamerProperty> {

    public Gamer(final UUID uuid) {
        super(uuid);
    }

    public Gamer(final Player player) {
        this(player.getUniqueId());
    }

    public Gamer(final EnumData<GamerProperty> data) {
        super(data);
    }

    @Override
    public List<GamerProperty> getProperties() {
        return Arrays.asList(GamerProperty.values());
    }

    @Override
    public Object getValueByProperty(final GamerProperty property) {
        return null;
    }

    @Override
    public LinkedHashMap<String, String> getInformation() {
        return new LinkedHashMap<>();
    }
}