package me.trae.champions.role.types;

import me.trae.api.champions.role.Role;
import me.trae.champions.role.RoleManager;
import me.trae.champions.role.types.models.Archer;
import me.trae.champions.skill.skills.ranger.axe.Agility;
import me.trae.champions.skill.skills.ranger.axe.WolfsFury;
import me.trae.champions.skill.skills.ranger.bow.IncendiaryShot;
import me.trae.champions.skill.skills.ranger.bow.PinDown;
import me.trae.champions.skill.skills.ranger.bow.RopedArrow;
import me.trae.champions.skill.skills.ranger.passive_a.Longshot;
import me.trae.champions.skill.skills.ranger.passive_a.Precision;
import me.trae.champions.skill.skills.ranger.passive_a.Sharpshooter;
import me.trae.champions.skill.skills.ranger.passive_b.Entangle;
import me.trae.champions.skill.skills.ranger.passive_b.HuntersThrill;
import me.trae.champions.skill.skills.ranger.sword.Disengage;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.List;

public class Ranger extends Role implements Archer {

    public Ranger(final RoleManager manager) {
        super(manager);
    }

    @Override
    public void registerSubModules() {
        // Sword Skills
        addSubModule(new Disengage(this));

        // Axe Skills
        addSubModule(new Agility(this));
        addSubModule(new WolfsFury(this));

        // Bow Skills
        addSubModule(new IncendiaryShot(this));
        addSubModule(new PinDown(this));
        addSubModule(new RopedArrow(this));

        // Passive A Skills
        addSubModule(new Longshot(this));
        addSubModule(new Precision(this));
        addSubModule(new Sharpshooter(this));

        // Passive B Skills
        addSubModule(new Entangle(this));
        addSubModule(new HuntersThrill(this));

        super.registerSubModules();
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Rangers are masters of the bow",
                "sniping foes from afar!"
        };
    }

    @Override
    public List<Material> getArmour() {
        return Arrays.asList(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS);
    }

    @Override
    public SoundCreator getDamageSound() {
        return new SoundCreator(Sound.ITEM_BREAK, 1.0F, 1.4F);
    }
}