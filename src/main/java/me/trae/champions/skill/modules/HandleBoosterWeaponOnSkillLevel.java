package me.trae.champions.skill.modules;

import me.trae.api.champions.skill.Skill;
import me.trae.api.champions.skill.events.SkillLevelEvent;
import me.trae.champions.Champions;
import me.trae.champions.skill.SkillManager;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.interfaces.IPassiveBowSkill;
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
        if (!(this.isValid(event.getSkill()))) {
            return;
        }

        if (!(UtilChampions.isBoosterWeapon(event.getPlayer().getEquipment().getItemInHand()))) {
            return;
        }

        event.setLevel(event.getLevel() + 1);
    }

    private boolean isValid(final Skill<?, ?> skill) {
        if (skill instanceof ActiveSkill<?, ?> && Arrays.asList(SkillType.SWORD, SkillType.AXE, SkillType.BOW).contains(skill.getType())) {
            return true;
        }

        if (skill instanceof IPassiveBowSkill<?> && skill.getType().isPassive()) {
            return true;
        }

        return false;
    }
}