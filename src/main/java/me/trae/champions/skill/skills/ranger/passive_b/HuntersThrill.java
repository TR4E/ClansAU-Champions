package me.trae.champions.skill.skills.ranger.passive_b;

import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.role.types.Ranger;
import me.trae.champions.skill.skills.ranger.passive_b.data.HuntersThrillData;
import me.trae.champions.skill.types.PassiveBowSkill;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.updater.annotations.Update;
import me.trae.core.updater.interfaces.Updater;
import me.trae.core.utility.UtilEntity;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.UtilTime;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class HuntersThrill extends PassiveBowSkill<Ranger, HuntersThrillData> implements Updater {

    @ConfigInject(type = Integer.class, path = "Max-Charges", defaultValue = "2")
    private int maxCharges;

    @ConfigInject(type = Long.class, path = "Duration", defaultValue = "4_000")
    private long duration;

    @ConfigInject(type = Long.class, path = "Last-Hit-Expiration", defaultValue = "8_000")
    private long lastHitExpiration;

    public HuntersThrill(final Ranger module) {
        super(module, PassiveSkillType.PASSIVE_B);
    }

    @Override
    public Class<HuntersThrillData> getClassOfData() {
        return HuntersThrillData.class;
    }

    private int getMaxCharges(final int level) {
        return this.maxCharges + (level - 1);
    }

    private long getDuration(final int level) {
        return this.duration + ((level - 1) * 1000L);
    }

    private long getLastHitExpiration(final int level) {
        return this.lastHitExpiration + (level * 1000L);
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "For each consecutive hit,",
                UtilString.format("within %s of each other,", this.getValueString(Long.class, this::getDuration, level)),
                "you gain increased movement speed.",
                "",
                UtilString.format("You can store a maximum of <green>%s</green> charges.", this.getMaxCharges(level)),
        };
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean resetDataOnShoot() {
        return false;
    }

    @Override
    public void onActivate(final Player player, final int level) {
        this.addUser(new HuntersThrillData(player, level));
    }

    @Override
    public void reset(final Player player) {
        if (this.isUserByPlayer(player)) {
            UtilEntity.removePotionEffect(player, PotionEffectType.SPEED);
        }

        super.reset(player);
    }

    @Override
    public void onHitByEntity(final Player damager, final Entity damagee, final CustomPostDamageEvent event, final HuntersThrillData data) {
        if (!(damagee instanceof Player)) {
            return;
        }

        if (data.getCharges() >= this.getMaxCharges(data.getLevel())) {
            return;
        }

        data.addCharge();

        data.updateLastHit();

        UtilEntity.givePotionEffect(damager, PotionEffectType.SPEED, data.getCharges(), this.getDuration(data.getLevel()));

        UtilMessage.simpleMessage(damager, this.getName(), UtilString.pair("Charges", UtilString.format("<yellow>%s", data.getCharges())));
    }

    @Update(delay = 250L)
    public void onUpdater() {
        this.getUsers().values().removeIf(data -> {
            if (!(UtilTime.elapsed(data.getLastHit(), this.getLastHitExpiration(data.getLevel())))) {
                return false;
            }

            final Player player = Bukkit.getPlayer(data.getUUID());
            if (player != null) {
                UtilMessage.simpleMessage(player, this.getModule().getName(), "<green><var></green> has ended.", Collections.singletonList(this.getDisplayName(data.getLevel())));
            }

            return true;
        });
    }
}