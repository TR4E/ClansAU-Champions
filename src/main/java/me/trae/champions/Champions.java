package me.trae.champions;

import me.trae.champions.build.BuildManager;
import me.trae.champions.config.ConfigManager;
import me.trae.champions.donation.DonationManager;
import me.trae.champions.effect.EffectManager;
import me.trae.champions.perk.PerkManager;
import me.trae.champions.preference.PreferenceManager;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.tip.TipManager;
import me.trae.champions.weapon.WeaponManager;
import me.trae.champions.world.WorldManager;
import me.trae.core.blood.BloodManager;
import me.trae.core.framework.types.plugin.MiniPlugin;

public class Champions extends MiniPlugin {

    @Override
    public void registerManagers() {
        // Core Managers
        addManager(new BloodManager<>(this));

        // Champions Managers
        addManager(new BuildManager(this));
        addManager(new ConfigManager(this));
        addManager(new DonationManager(this));
        addManager(new EffectManager(this));
//        addManager(new GamerManager(this));
        addManager(new PerkManager(this));
        addManager(new PreferenceManager(this));
        addManager(new RoleManager(this));
        addManager(new SkillManager(this));
        addManager(new TipManager(this));
        addManager(new WeaponManager(this));
        addManager(new WorldManager(this));
    }
}