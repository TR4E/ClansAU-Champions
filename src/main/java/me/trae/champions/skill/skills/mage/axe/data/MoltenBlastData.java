package me.trae.champions.skill.skills.mage.axe.data;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.skills.mage.axe.data.interfaces.IMoltenBlastData;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;

public class MoltenBlastData extends SkillData implements IMoltenBlastData {

    private LargeFireball fireball;

    public MoltenBlastData(final Player player, final int level, final long duration) {
        super(player, level, duration);
    }

    @Override
    public LargeFireball getFireBall() {
        return this.fireball;
    }

    @Override
    public void setFireBall(final LargeFireball fireBall) {
        this.fireball = fireBall;
    }
}