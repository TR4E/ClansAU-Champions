package me.trae.champions.role.types;

import me.trae.api.champions.role.Role;
import me.trae.champions.role.RoleManager;
import me.trae.champions.skill.skills.knight.axe.BullsCharge;
import me.trae.champions.skill.skills.knight.passive_b.Swordsmanship;
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
        // Axe Skills
        addSubModule(new BullsCharge(this));

        // Passive B Skills
        addSubModule(new Swordsmanship(this));
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