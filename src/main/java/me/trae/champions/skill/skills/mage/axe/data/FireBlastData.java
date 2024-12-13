package me.trae.champions.skill.skills.mage.axe.data;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.skills.mage.axe.data.interfaces.IFireBlastData;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;

public class FireBlastData extends SkillData implements IFireBlastData {

    private LargeFireball fireball;

    public FireBlastData(final Player player, final int level) {
        super(player, level);
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