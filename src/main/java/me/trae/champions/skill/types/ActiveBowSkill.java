package me.trae.champions.skill.types;

import me.trae.api.champions.role.Role;
import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.skill.types.data.BowSkillData;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.champions.skill.types.interfaces.IActiveBowSkill;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collections;

public abstract class ActiveBowSkill<R extends Role, D extends BowSkillData> extends ActiveSkill<R, D> implements IActiveBowSkill<D> {

    public ActiveBowSkill(final R module) {
        super(module, ActiveSkillType.BOW);
    }

    @Override
    public void onActivate(final Player player, final int level) {
        new SoundCreator(Sound.BLAZE_BREATH, 2.5F, 2.0F).play(player.getLocation());

        this.onPrepare(player, level);
    }

    @Override
    public boolean isActive(final Player player) {
        final D data = this.getUserByPlayer(player);
        if (data != null) {
            if (data.hasArrow()) {
                UtilMessage.simpleMessage(player, this.getModule().getName(), "You already fired <green><var></green>.", Collections.singletonList(this.getName()));
            } else {
                UtilMessage.simpleMessage(player, this.getModule().getName(), "You already prepared <green><var></green>.", Collections.singletonList(this.getName()));
            }

            return true;
        }

        return false;
    }

    @Override
    public void onPrepare(final Player player, final int level) {
        UtilMessage.simpleMessage(player, this.getModule().getName(), "You have prepared <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @Override
    public void onFire(final Player player, final D data) {
        UtilMessage.simpleMessage(player, this.getModule().getName(), "You have fired <green><var></green>.", Collections.singletonList(this.getDisplayName(data.getLevel())));
    }

    @Override
    public void onHitByLocation(final Player player, final Location location, final D data) {
    }

    @Override
    public void onHitByEntity(final Player damager, final Entity damagee, final CustomPostDamageEvent event, final D data) {
    }

    @Override
    public void onUpdater(final Player player, final D data) {
    }
}