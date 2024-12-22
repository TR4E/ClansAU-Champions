package me.trae.champions.role.types;

import me.trae.api.champions.role.Role;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.skills.knight.axe.BullsCharge;
import me.trae.champions.skill.skills.knight.axe.HoldPosition;
import me.trae.champions.skill.skills.knight.axe.PowerChop;
import me.trae.champions.skill.skills.knight.passive_a.Cleave;
import me.trae.champions.skill.skills.knight.passive_a.Fury;
import me.trae.champions.skill.skills.knight.passive_a.Thorns;
import me.trae.champions.skill.skills.knight.passive_b.Sacrifice;
import me.trae.champions.skill.skills.knight.passive_b.Swordsmanship;
import me.trae.champions.skill.skills.knight.sword.DefensiveStance;
import me.trae.champions.skill.skills.knight.sword.Riposte;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.List;

public class Knight extends Role {

    public Knight(final RoleManager manager) {
        super(manager);
    }

    @Override
    public void registerSubModules() {
        // Sword Skills
        addSubModule(new DefensiveStance(this));
        addSubModule(new Riposte(this));

        // Axe Skills
        addSubModule(new BullsCharge(this));
        addSubModule(new HoldPosition(this));
        addSubModule(new PowerChop(this));

        // Passive A Skills
        addSubModule(new Cleave(this));
        addSubModule(new Fury(this));
        addSubModule(new Thorns(this));

        // Passive B Skills
        addSubModule(new Sacrifice(this));
        addSubModule(new Swordsmanship(this));

        super.registerSubModules();
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Knights are sturdy fighters,",
                "built to tank damage!"
        };
    }

    @Override
    public List<Material> getArmour() {
        return Arrays.asList(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS);
    }

    @Override
    public SoundCreator getDamageSound() {
        return new SoundCreator(Sound.BLAZE_HIT, 1.0F, 0.7F);
    }
}