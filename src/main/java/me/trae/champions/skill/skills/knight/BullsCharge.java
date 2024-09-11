package me.trae.champions.skill.skills.knight;

import me.trae.champions.role.types.Knight;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.ActiveSkill;
import org.bukkit.entity.Player;

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
    }

    @Override
    public boolean canActivate(final Player player) {
        return false;
    }
}