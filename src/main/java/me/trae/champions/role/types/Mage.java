package me.trae.champions.role.types;

import me.trae.api.champions.role.Role;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.skills.mage.axe.DefensiveAura;
import me.trae.champions.skill.skills.mage.axe.MoltenBlast;
import me.trae.champions.skill.skills.mage.passive_a.MagmaBlade;
import me.trae.champions.skill.skills.mage.passive_a.MoltenShield;
import me.trae.champions.skill.skills.mage.passive_a.NullBlade;
import me.trae.champions.skill.skills.mage.passive_b.HolyLight;
import me.trae.champions.skill.skills.mage.passive_b.Immolate;
import me.trae.champions.skill.skills.mage.sword.Cyclone;
import me.trae.champions.skill.skills.mage.sword.GlacialPrison;
import me.trae.champions.skill.skills.mage.sword.Inferno;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.List;

public class Mage extends Role {

    public Mage(final RoleManager manager) {
        super(manager);
    }

    @Override
    public void registerSubModules() {
        // Sword Skills
        addSubModule(new Cyclone(this));
        addSubModule(new GlacialPrison(this));
        addSubModule(new Inferno(this));

        // Axe Skills
        addSubModule(new DefensiveAura(this));
        addSubModule(new MoltenBlast(this));

        // Passive A Skills
        addSubModule(new MagmaBlade(this));
        addSubModule(new MoltenShield(this));
        addSubModule(new NullBlade(this));

        // Passive B Skills
        addSubModule(new HolyLight(this));
        addSubModule(new Immolate(this));

        super.registerSubModules();
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Mages are known for powerful",
                "spells and can fill most roles"
        };
    }

    @Override
    public List<Material> getArmour() {
        return Arrays.asList(Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS);
    }

    @Override
    public SoundCreator getDamageSound() {
        return new SoundCreator(Sound.ITEM_BREAK, 1.0F, 1.8F);
    }
}