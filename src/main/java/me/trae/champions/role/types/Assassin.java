package me.trae.champions.role.types;

import me.trae.api.champions.role.Role;
import me.trae.champions.role.RoleManager;
import me.trae.champions.role.types.models.Archer;
import me.trae.champions.role.types.submodules.DisableFallDamage;
import me.trae.champions.role.types.submodules.SpeedEffect;
import me.trae.champions.role.types.submodules.TakeNoKnockback;
import me.trae.champions.skill.skills.assassin.axe.Leap;
import me.trae.champions.skill.skills.assassin.bow.SilencingArrow;
import me.trae.champions.skill.skills.assassin.passive_a.ShockingStrikes;
import me.trae.champions.skill.skills.assassin.passive_b.SmokeBomb;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.List;

public class Assassin extends Role implements Archer {

    public Assassin(final RoleManager manager) {
        super(manager);
    }

    @Override
    public void registerSubModules() {
        // Sub Modules
        addSubModule(new DisableFallDamage(this));
        addSubModule(new SpeedEffect(this));
        addSubModule(new TakeNoKnockback(this));

        // Axe Skills
        addSubModule(new Leap(this));

        // Active Bow Skills
        addSubModule(new SilencingArrow(this));

        // Passive A Skills
        addSubModule(new ShockingStrikes(this));

        // Passive B Skills
        addSubModule(new SmokeBomb(this));

        super.registerSubModules();
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Assassins can quickly drop foes",
                "with powerful combinations!"
        };
    }

    @Override
    public List<Material> getArmour() {
        return Arrays.asList(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);
    }

    @Override
    public SoundCreator getDamageSound() {
        return new SoundCreator(Sound.SHOOT_ARROW, 1.0F, 2.0F);
    }
}