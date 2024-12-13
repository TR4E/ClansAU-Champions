package me.trae.champions.skill.skills.ranger.bow.data;

import me.trae.champions.skill.skills.ranger.bow.data.interfaces.IPinDownData;
import me.trae.champions.skill.types.data.BowSkillData;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PinDownData extends BowSkillData implements IPinDownData {

    private final List<Arrow> extraArrows;

    public PinDownData(final Player player, final int level) {
        super(player, level);

        this.extraArrows = new ArrayList<>();
    }

    @Override
    public List<Arrow> getExtraArrows() {
        return this.extraArrows;
    }

    @Override
    public void addExtraArrow(final Arrow arrow) {
        this.getExtraArrows().add(arrow);
    }
}