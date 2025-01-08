package me.trae.champions.skill.skills.ranger.passive_a;

import me.trae.champions.role.types.Ranger;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.types.PassiveSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.Core;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.gamer.Gamer;
import me.trae.core.gamer.GamerManager;
import me.trae.core.updater.annotations.Update;
import me.trae.core.updater.interfaces.Updater;
import me.trae.core.utility.UtilEntity;
import me.trae.core.utility.UtilTime;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class VitalitySpores extends PassiveSkill<Ranger, SkillData> implements Updater {

    private final GamerManager GAMER_MANAGER;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "5_000")
    private long duration;

    @ConfigInject(type = Integer.class, path = "Regeneration-Amplifier", defaultValue = "1")
    private int regenerationAmplifier;

    @ConfigInject(type = Long.class, path = "Regeneration-Duration", defaultValue = "6_000")
    private long regenerationDuration;

    public VitalitySpores(final Ranger module) {
        super(module, PassiveSkillType.PASSIVE_A);

        this.GAMER_MANAGER = this.getInstanceByClass(Core.class).getManagerByClass(GamerManager.class);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private long getDuration(final int level) {
        return this.duration - (level * 1000L);
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                String.format("After <green>%s</green> of not taking damage,", UtilTime.getTime(this.getDuration(level))),
                "forest spores surround you, giving",
                String.format("you Regeneration %s for %s", this.regenerationAmplifier, UtilTime.getTime(this.regenerationDuration)),
                "",
                "This remains until you take damage."
        };
    }

    @Update(delay = 250L)
    public void onUpdater() {
        for (final Player player : this.getPlayers()) {
            final int level = this.getLevel(player);
            if (level == 0) {
                continue;
            }

            final Gamer gamer = this.GAMER_MANAGER.getGamerByPlayer(player);

            if (!(UtilTime.elapsed(gamer.getLastDamaged(), this.getDuration(level)))) {
                if (UtilEntity.hasPotionEffect(player, PotionEffectType.REGENERATION)) {
                    UtilEntity.removePotionEffect(player, PotionEffectType.REGENERATION);
                }
                continue;
            }

            if (UtilEntity.hasPotionEffect(player, PotionEffectType.REGENERATION, this.regenerationAmplifier)) {
                continue;
            }

            UtilEntity.givePotionEffect(player, PotionEffectType.REGENERATION, this.regenerationAmplifier, this.regenerationDuration);
        }
    }
}