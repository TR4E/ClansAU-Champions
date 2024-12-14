package me.trae.champions.role.types;

import me.trae.api.champions.role.Role;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.skills.brute.axe.*;
import me.trae.champions.skill.skills.brute.passive_a.Colossus;
import me.trae.champions.skill.skills.brute.passive_a.Resistance;
import me.trae.champions.skill.skills.brute.passive_b.Bloodlust;
import me.trae.champions.skill.skills.brute.passive_b.Stampede;
import me.trae.champions.skill.skills.brute.sword.BattleTaunt;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.List;

public class Brute extends Role {

    public Brute(final RoleManager manager) {
        super(manager);
    }

    @Override
    public void registerSubModules() {
        // Sword Skills
        addSubModule(new BattleTaunt(this));

        // Axe Skills
        addSubModule(new SeismicSlam(this));
        addSubModule(new SpiritOfTheBear(this));
        addSubModule(new SpiritOfTheWolf(this));
        addSubModule(new StrengthInNumbers(this));
        addSubModule(new ThreateningShout(this));

        // Passive A Skills
        addSubModule(new Colossus(this));
        addSubModule(new Resistance(this));

        // Passive B Skills
        addSubModule(new Bloodlust(this));
        addSubModule(new Stampede(this));

        super.registerSubModules();
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Brutes control large crowds of enemies,",
                "with their unique abilities!"
        };
    }

    @Override
    public List<Material> getArmour() {
        return Arrays.asList(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS);
    }

    @Override
    public SoundCreator getDamageSound() {
        return new SoundCreator(Sound.BLAZE_HIT, 1.0F, 0.9F);
    }
}