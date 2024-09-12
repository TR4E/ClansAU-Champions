package me.trae.champions.skill.skills.knight;

import me.trae.champions.role.types.Knight;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.core.utility.UtilBlock;
import me.trae.core.utility.UtilMessage;
import org.bukkit.entity.Player;

import java.util.Collections;

public class BullsCharge extends ActiveSkill<Knight, SkillData> {

    public BullsCharge(final Knight module) {
        super(module, SkillType.AXE);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    @Override
    public void onActivate(final Player player) {
        UtilMessage.simpleMessage(player, this.getModule().getName(), "You used <green><var></green>.", Collections.singletonList(this.getName()));
    }

    @Override
    public boolean canActivate(final Player player) {
        if (UtilBlock.isInLiquid(player.getLocation())) {
            UtilMessage.simpleMessage(player, "Skill", "You cannot use <green><var></green> while in liquid.", Collections.singletonList(this.getName()));
            return false;
        }

        return true;
    }
}