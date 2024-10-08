package me.trae.champions.role.types;

import me.trae.champions.role.Role;
import me.trae.champions.role.RoleManager;
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