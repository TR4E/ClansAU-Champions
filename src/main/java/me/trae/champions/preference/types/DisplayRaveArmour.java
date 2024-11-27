package me.trae.champions.preference.types;

import me.trae.champions.Champions;
import me.trae.champions.perk.perks.RaveArmour;
import me.trae.champions.preference.PreferenceManager;
import me.trae.core.perk.registry.PerkRegistry;
import me.trae.core.preference.Preference;
import me.trae.core.preference.models.BooleanPreference;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class DisplayRaveArmour extends Preference<Champions, PreferenceManager, Boolean> implements BooleanPreference, Listener {

    public DisplayRaveArmour(final PreferenceManager manager) {
        super(manager);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Toggles the visibility of the <gold>Rave Armour</gold> for Leather armour."
        };
    }

    @Override
    public boolean showToPlayer(final Player player) {
        return PerkRegistry.getPerkByClass(RaveArmour.class).isUserByPlayer(player);
    }
}