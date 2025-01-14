package me.trae.champions.skill.skills.mage.passive_a;

import me.trae.champions.role.types.Mage;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.Core;
import me.trae.core.effect.EffectManager;
import me.trae.core.effect.data.EffectData;
import me.trae.core.effect.types.FireResistance;
import me.trae.core.updater.annotations.Update;
import me.trae.core.updater.interfaces.Updater;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class MoltenShield extends PassiveSkill<Mage, SkillData> implements Listener, Updater {

    private final FireResistance EFFECT;

    public MoltenShield(final Mage module) {
        super(module, PassiveSkillType.PASSIVE_A);

        this.EFFECT = this.getInstanceByClass(Core.class).getManagerByClass(EffectManager.class).getModuleByClass(FireResistance.class);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "You are immune to lava and fire damage."
        };
    }

    @Override
    public int getDefaultLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            this.EFFECT.removeUser(player);
        }
    }

    @Update(delay = 250L)
    public void onUpdater() {
        for (final Player player : this.getPlayers()) {
            if (!(this.isUserByPlayer(player))) {
                this.addUser(new SkillData(player, this.getLevel(player)));
            }

            if (player.getFireTicks() > 0) {
                player.setFireTicks(0);
            }

            if (!(this.EFFECT.isUserByEntity(player))) {
                this.EFFECT.addUser(new EffectData(player));
            }
        }
    }
}