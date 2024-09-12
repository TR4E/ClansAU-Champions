package me.trae.champions.skill.modules;

import me.trae.api.champions.skill.SkillLevelEvent;
import me.trae.champions.Champions;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.utility.UtilChampions;
import me.trae.core.framework.types.frame.SpigotListener;
import org.bukkit.event.EventHandler;

import java.util.Arrays;

public class HandleBoosterWeaponOnSkillLevel extends SpigotListener<Champions, SkillManager> {

    public HandleBoosterWeaponOnSkillLevel(final SkillManager manager) {
        super(manager);
    }

    @EventHandler
    public void onSkillLevel(final SkillLevelEvent event) {
        if (!(Arrays.asList(SkillType.SWORD, SkillType.AXE).contains(event.getSkill().getType()))) {
            return;
        }

        if (!(UtilChampions.isBoosterWeapon(event.getPlayer().getInventory().getItemInHand()))) {
            return;
        }

        event.setLevel(event.getLevel() + 1);
    }
}